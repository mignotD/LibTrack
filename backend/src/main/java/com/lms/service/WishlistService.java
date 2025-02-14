package com.lms.service;

import com.lms.dto.response.BookResponse;
import com.lms.entity.Book;
import com.lms.entity.Member;
import com.lms.entity.Wishlist;
import com.lms.exception.BadRequestException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.BookRepository;
import com.lms.repository.MemberRepository;
import com.lms.repository.WishlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {
    private static final Logger log = LoggerFactory.getLogger(WishlistService.class);

    private final WishlistRepository wishlistRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public WishlistService(WishlistRepository wishlistRepository, BookRepository bookRepository,
                           MemberRepository memberRepository) {
        this.wishlistRepository = wishlistRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    public List<BookResponse> getByMember(Integer memberId) {
        return wishlistRepository.findByMemberMemberId(memberId).stream()
                .map(w -> BookResponse.from(w.getBook()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void add(Integer memberId, String isbn) {
        if (wishlistRepository.existsByBookIsbnAndMemberMemberId(isbn, memberId)) {
            throw new BadRequestException("Book already in wishlist");
        }
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + isbn));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        wishlistRepository.save(new Wishlist(book, member));
        log.info("Book {} added to wishlist of member {}", isbn, memberId);
    }

    @Transactional
    public void remove(Integer memberId, String isbn) {
        wishlistRepository.deleteByBookIsbnAndMemberMemberId(isbn, memberId);
        log.info("Book {} removed from wishlist of member {}", isbn, memberId);
    }
}
