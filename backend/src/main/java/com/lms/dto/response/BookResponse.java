package com.lms.dto.response;

import com.lms.entity.Book;

public class BookResponse {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String genre;
    private Integer publicationYear;
    private Integer stock;
    private Integer available;
    private String coverUrl;
    private double averageRating;
    private long reviewCount;

    public static BookResponse from(Book book) {
        BookResponse r = new BookResponse();
        r.isbn = book.getIsbn();
        r.title = book.getTitle();
        r.author = book.getAuthor();
        r.publisher = book.getPublisher();
        r.genre = book.getGenre();
        r.publicationYear = book.getPublicationYear();
        r.stock = book.getStock();
        r.available = book.getAvailable();
        r.coverUrl = book.getCoverUrl();
        return r;
    }

    public static BookResponse from(Book book, double avgRating, long reviewCount) {
        BookResponse r = from(book);
        r.averageRating = avgRating;
        r.reviewCount = reviewCount;
        return r;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getGenre() { return genre; }
    public Integer getPublicationYear() { return publicationYear; }
    public Integer getStock() { return stock; }
    public Integer getAvailable() { return available; }
    public String getCoverUrl() { return coverUrl; }
    public double getAverageRating() { return averageRating; }
    public long getReviewCount() { return reviewCount; }
}
