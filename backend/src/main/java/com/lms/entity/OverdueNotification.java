package com.lms.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "overdue_notifications")
public class OverdueNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(nullable = false, length = 20)
    private String isbn;

    @Column(name = "notified_date")
    private LocalDateTime notifiedDate = LocalDateTime.now();

    public OverdueNotification() {}

    public OverdueNotification(Integer memberId, String isbn) {
        this.memberId = memberId;
        this.isbn = isbn;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public LocalDateTime getNotifiedDate() { return notifiedDate; }
    public void setNotifiedDate(LocalDateTime notifiedDate) { this.notifiedDate = notifiedDate; }
}
