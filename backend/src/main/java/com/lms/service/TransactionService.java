package com.lms.service;

import com.lms.dto.response.TransactionResponse;
import com.lms.entity.Book;
import com.lms.entity.BorrowingLimit;
import com.lms.entity.Member;
import com.lms.entity.Transaction;
import com.lms.exception.BadRequestException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowingLimitRepository limitRepository;
    private final ReservationRepository reservationRepository;

    public TransactionService(TransactionRepository transactionRepository, BookRepository bookRepository,
                              MemberRepository memberRepository, BorrowingLimitRepository limitRepository,
                              ReservationRepository reservationRepository) {
        this.transactionRepository = transactionRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.limitRepository = limitRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<TransactionResponse> getByMember(Integer memberId) {
        return transactionRepository.findByMemberMemberIdOrderByBorrowDateDesc(memberId).stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getActiveByMember(Integer memberId) {
        return transactionRepository.findByMemberMemberIdOrderByBorrowDateDesc(memberId).stream()
                .filter(t -> t.getReturnDate() == null)
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getAll() {
        return transactionRepository.findAll().stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getOverdue() {
        return transactionRepository.findOverdueTransactions(LocalDate.now()).stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponse borrowBook(Integer memberId, String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + isbn));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (book.getAvailable() <= 0) {
            throw new BadRequestException("Book is not available");
        }

        long activeLoans = transactionRepository.countByMemberMemberIdAndReturnDateIsNull(memberId);
        BorrowingLimit limit = limitRepository.findByMemberTypeIgnoreCase(member.getRole())
                .orElse(new BorrowingLimit());

        if (activeLoans >= limit.getMaxBooks()) {
            throw new BadRequestException("Borrowing limit reached (" + limit.getMaxBooks() + " books)");
        }

        book.setAvailable(book.getAvailable() - 1);
        bookRepository.save(book);

        Transaction t = new Transaction(book, member, LocalDate.now(), LocalDate.now().plusDays(limit.getLoanDurationDays()));
        t = transactionRepository.save(t);

        // Fulfill reservation if exists
        reservationRepository.findByBookIsbnAndMemberMemberIdAndFulfilledFalse(isbn, memberId)
                .ifPresent(r -> {
                    r.setFulfilled(true);
                    reservationRepository.save(r);
                });

        log.info("Book {} borrowed by member {}", isbn, memberId);
        return TransactionResponse.from(t);
    }

    @Transactional
    public TransactionResponse returnBook(Integer transactionId, Integer fineAmount) {
        Transaction t = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        if (t.getReturnDate() != null) {
            throw new BadRequestException("Book already returned");
        }

        t.setReturnDate(LocalDate.now());

        long overdueDays = ChronoUnit.DAYS.between(t.getDueDate(), LocalDate.now());
        if (overdueDays > 0) {
            int fine = (fineAmount != null && fineAmount > 0) ? fineAmount : (int) (overdueDays * 5);
            t.setFine(BigDecimal.valueOf(fine));
        }

        t = transactionRepository.save(t);

        Book book = t.getBook();
        book.setAvailable(book.getAvailable() + 1);
        bookRepository.save(book);

        log.info("Book {} returned by member {}", t.getBook().getIsbn(), t.getMember().getMemberId());
        return TransactionResponse.from(t);
    }

    @Transactional
    public TransactionResponse renewBook(Integer transactionId) {
        Transaction t = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        if (t.getReturnDate() != null) {
            throw new BadRequestException("Book already returned");
        }

        t.setDueDate(t.getDueDate().plusDays(14));
        t = transactionRepository.save(t);
        log.info("Transaction {} renewed", transactionId);
        return TransactionResponse.from(t);
    }

    @Transactional
    public void payFine(Integer transactionId) {
        Transaction t = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));
        t.setFinePaid(true);
        transactionRepository.save(t);
        log.info("Fine paid for transaction {}", transactionId);
    }
}
