package com.lms.dto.request;

import javax.validation.constraints.NotBlank;

public class BorrowRequest {
    @NotBlank
    private String isbn;

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}
