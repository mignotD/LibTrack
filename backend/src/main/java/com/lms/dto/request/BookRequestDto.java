package com.lms.dto.request;

import javax.validation.constraints.NotBlank;

public class BookRequestDto {
    @NotBlank
    private String title;
    private String author;
    private String publisher;
    private String isbn;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}
