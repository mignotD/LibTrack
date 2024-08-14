package com.lms.repository;

import com.lms.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByBookIsbnOrderByReviewDateDesc(String isbn);
    List<Review> findByMemberMemberId(Integer memberId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.book.isbn = :isbn")
    double averageRatingByBookIsbn(@Param("isbn") String isbn);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.isbn = :isbn")
    long countByBookIsbn(@Param("isbn") String isbn);
}
