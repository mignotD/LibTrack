package com.lms.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @Column(name = "isbn", length = 20)
    private String isbn;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String author;

    private String publisher;
    private String genre;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false)
    private Integer available = 0;

    @Column(name = "cover_url")
    private String coverUrl;

    @OneToMany(mappedBy = "book")
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<Wishlist> wishlistEntries = new ArrayList<>();

    public Book() {}

    public Book(String isbn, String title, String author, String publisher,
                String genre, Integer publicationYear, Integer stock, Integer available) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.stock = stock;
        this.available = available;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getAvailable() { return available; }
    public void setAvailable(Integer available) { this.available = available; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public List<Transaction> getTransactions() { return transactions; }
    public List<Review> getReviews() { return reviews; }
    public List<Wishlist> getWishlistEntries() { return wishlistEntries; }
}
