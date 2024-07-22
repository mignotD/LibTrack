package com.lms.entity;

import javax.persistence.*;

@Entity
@Table(name = "borrowing_limits")
public class BorrowingLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "limit_id")
    private Integer limitId;

    @Column(name = "member_type", nullable = false, unique = true, length = 50)
    private String memberType;

    @Column(name = "max_books", nullable = false)
    private Integer maxBooks = 5;

    @Column(name = "loan_duration_days", nullable = false)
    private Integer loanDurationDays = 14;

    public BorrowingLimit() {}

    public Integer getLimitId() { return limitId; }
    public void setLimitId(Integer limitId) { this.limitId = limitId; }
    public String getMemberType() { return memberType; }
    public void setMemberType(String memberType) { this.memberType = memberType; }
    public Integer getMaxBooks() { return maxBooks; }
    public void setMaxBooks(Integer maxBooks) { this.maxBooks = maxBooks; }
    public Integer getLoanDurationDays() { return loanDurationDays; }
    public void setLoanDurationDays(Integer loanDurationDays) { this.loanDurationDays = loanDurationDays; }
}
