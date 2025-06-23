package com.lms.dto.request;

import javax.validation.constraints.NotNull;

public class ReturnRequest {
    @NotNull
    private Integer transactionId;
    private Integer fine;

    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }
    public Integer getFine() { return fine; }
    public void setFine(Integer fine) { this.fine = fine; }
}
