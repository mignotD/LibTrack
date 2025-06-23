package com.lms.dto.response;

import com.lms.entity.Review;

import java.time.LocalDateTime;

public class ReviewResponse {
    private Integer reviewId;
    private String isbn;
    private Integer memberId;
    private String memberName;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;

    public static ReviewResponse from(Review r) {
        ReviewResponse res = new ReviewResponse();
        res.reviewId = r.getReviewId();
        res.isbn = r.getBook().getIsbn();
        res.memberId = r.getMember().getMemberId();
        res.memberName = r.getMember().getName();
        res.rating = r.getRating();
        res.comment = r.getComment();
        res.reviewDate = r.getReviewDate();
        return res;
    }

    public Integer getReviewId() { return reviewId; }
    public String getIsbn() { return isbn; }
    public Integer getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getReviewDate() { return reviewDate; }
}
