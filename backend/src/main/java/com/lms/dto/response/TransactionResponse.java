package com.lms.dto.response;

import com.lms.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionResponse {
    private Integer transactionId;
    private String isbn;
    private String bookTitle;
    private String bookAuthor;
    private Integer memberId;
    private String memberName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BigDecimal fine;
    private boolean finePaid;

    public static TransactionResponse from(Transaction t) {
        TransactionResponse r = new TransactionResponse();
        r.transactionId = t.getTransactionId();
        r.isbn = t.getBook().getIsbn();
        r.bookTitle = t.getBook().getTitle();
        r.bookAuthor = t.getBook().getAuthor();
        r.memberId = t.getMember().getMemberId();
        r.memberName = t.getMember().getName();
        r.borrowDate = t.getBorrowDate();
        r.dueDate = t.getDueDate();
        r.returnDate = t.getReturnDate();
        r.fine = t.getFine();
        r.finePaid = t.getFinePaid();
        return r;
    }

    public Integer getTransactionId() { return transactionId; }
    public String getIsbn() { return isbn; }
    public String getBookTitle() { return bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public Integer getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public BigDecimal getFine() { return fine; }
    public boolean isFinePaid() { return finePaid; }
}
