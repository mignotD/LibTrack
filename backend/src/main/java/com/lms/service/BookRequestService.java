package com.lms.service;

import com.lms.dto.request.BookRequestDto;
import com.lms.entity.BookRequest;
import com.lms.entity.Member;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.BookRequestRepository;
import com.lms.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookRequestService {
    private static final Logger log = LoggerFactory.getLogger(BookRequestService.class);

    private final BookRequestRepository bookRequestRepository;
    private final MemberRepository memberRepository;

    public BookRequestService(BookRequestRepository bookRequestRepository, MemberRepository memberRepository) {
        this.bookRequestRepository = bookRequestRepository;
        this.memberRepository = memberRepository;
    }

    public List<BookRequest> getByMember(Integer memberId) {
        return bookRequestRepository.findByMemberMemberIdOrderByRequestDateDesc(memberId);
    }

    @Transactional
    public BookRequest submit(Integer memberId, BookRequestDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        BookRequest req = new BookRequest();
        req.setMember(member);
        req.setTitle(dto.getTitle());
        req.setAuthor(dto.getAuthor());
        req.setPublisher(dto.getPublisher());
        req.setIsbn(dto.getIsbn());

        req = bookRequestRepository.save(req);
        log.info("Book request submitted by member {}", memberId);
        return req;
    }
}
