package com.lms.service;

import com.lms.dto.response.ReviewResponse;
import com.lms.entity.Book;
import com.lms.entity.Member;
import com.lms.entity.Review;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.BookRepository;
import com.lms.repository.MemberRepository;
import com.lms.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository,
                         MemberRepository memberRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    public List<ReviewResponse> getByBook(String isbn) {
        return reviewRepository.findByBookIsbnOrderByReviewDateDesc(isbn).stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse addReview(String isbn, Integer memberId, Integer rating, String comment) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + isbn));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Review review = new Review();
        review.setBook(book);
        review.setMember(member);
        review.setRating(rating);
        review.setComment(comment);

        review = reviewRepository.save(review);
        log.info("Review added by member {} for book {}", memberId, isbn);
        return ReviewResponse.from(review);
    }
}
