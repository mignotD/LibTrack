package com.lms.service;

import com.lms.dto.response.MemberResponse;
import com.lms.entity.*;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final BookRequestRepository bookRequestRepository;
    private final AuditLogRepository auditLogRepository;
    private final BorrowingLimitRepository limitRepository;
    private final ReviewRepository reviewRepository;

    public AdminService(MemberRepository memberRepository, BookRepository bookRepository,
                        TransactionRepository transactionRepository, BookRequestRepository bookRequestRepository,
                        AuditLogRepository auditLogRepository, BorrowingLimitRepository limitRepository,
                        ReviewRepository reviewRepository) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.transactionRepository = transactionRepository;
        this.bookRequestRepository = bookRequestRepository;
        this.auditLogRepository = auditLogRepository;
        this.limitRepository = limitRepository;
        this.reviewRepository = reviewRepository;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalBooks", bookRepository.count());
        stats.put("totalMembers", memberRepository.count());
        stats.put("activeLoans", transactionRepository.countActiveLoans());
        stats.put("pendingRequests", bookRequestRepository.countByStatus("Pending"));
        stats.put("unpaidFines", transactionRepository.totalUnpaidFines());
        return stats;
    }

    public List<MemberResponse> getMembers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> memberPage = memberRepository.findAll(pageable);
        return memberPage.stream()
                .map(m -> MemberResponse.from(m,
                        transactionRepository.countByMemberMemberIdAndReturnDateIsNull(m.getMemberId())))
                .collect(Collectors.toList());
    }

    public long getMemberCount() {
        return memberRepository.count();
    }

    @Transactional
    public void toggleMemberStatus(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));
        String newStatus = "Active".equals(member.getStatus()) ? "Inactive" : "Active";
        member.setStatus(newStatus);
        memberRepository.save(member);
        log.info("Member {} status changed to {}", memberId, newStatus);
    }

    public List<BookRequest> getBookRequests(String status) {
        if (status != null && !status.isBlank()) {
            return bookRequestRepository.findByStatusOrderByRequestDateDesc(status);
        }
        return bookRequestRepository.findAllByOrderByRequestDateDesc();
    }

    @Transactional
    public void approveBookRequest(Integer requestId) {
        BookRequest req = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + requestId));
        req.setStatus("Approved");
        bookRequestRepository.save(req);
        log.info("Book request {} approved", requestId);
    }

    @Transactional
    public void rejectBookRequest(Integer requestId) {
        BookRequest req = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + requestId));
        req.setStatus("Rejected");
        bookRequestRepository.save(req);
        log.info("Book request {} rejected", requestId);
    }

    public Page<AuditLog> getAuditLogs(int page, int size) {
        return auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
    }

    public long getAuditLogCount() {
        return auditLogRepository.count();
    }

    public List<BorrowingLimit> getBorrowingLimits() {
        return limitRepository.findAll();
    }

    @Transactional
    public BorrowingLimit updateBorrowingLimit(Integer limitId, Integer maxBooks, Integer loanDurationDays) {
        BorrowingLimit limit = limitRepository.findById(limitId)
                .orElseThrow(() -> new ResourceNotFoundException("Limit not found: " + limitId));
        if (maxBooks != null) limit.setMaxBooks(maxBooks);
        if (loanDurationDays != null) limit.setLoanDurationDays(loanDurationDays);
        return limitRepository.save(limit);
    }

    public List<Map<String, Object>> getPopularBooks() {
        return bookRepository.findPopularBooks().stream()
                .limit(20)
                .map(b -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("isbn", b.getIsbn());
                    m.put("title", b.getTitle());
                    m.put("author", b.getAuthor());
                    m.put("avgRating", reviewRepository.averageRatingByBookIsbn(b.getIsbn()));
                    m.put("reviewCount", reviewRepository.countByBookIsbn(b.getIsbn()));
                    return m;
                })
                .collect(Collectors.toList());
    }
}
