package com.lms.service;

import com.lms.dto.response.BookResponse;
import com.lms.entity.Book;
import com.lms.entity.Review;
import com.lms.exception.BadRequestException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.BookRepository;
import com.lms.repository.ReviewRepository;
import com.lms.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final TransactionRepository transactionRepository;

    public BookService(BookRepository bookRepository, ReviewRepository reviewRepository,
                       TransactionRepository transactionRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponseWithRating)
                .collect(Collectors.toList());
    }

    public BookResponse findByIsbn(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + isbn));
        return toResponseWithRating(book);
    }

    public List<BookResponse> search(String title, String author, String genre, String isbn) {
        return bookRepository.searchAdvanced(title, author, genre, isbn).stream()
                .map(this::toResponseWithRating)
                .collect(Collectors.toList());
    }

    public List<BookResponse> findByGenre(String genre) {
        return bookRepository.findByGenreContainingIgnoreCase(genre).stream()
                .map(this::toResponseWithRating)
                .collect(Collectors.toList());
    }

    public List<String> getGenres() {
        return bookRepository.findDistinctGenres();
    }

    public List<BookResponse> getPopular() {
        return bookRepository.findPopularBooks().stream()
                .limit(20)
                .map(this::toResponseWithRating)
                .collect(Collectors.toList());
    }

    public List<BookResponse> getRecommendations(String isbn) {
        List<String> relatedIsbns = transactionRepository
                .findByBookIsbnAndReturnDateIsNull(isbn).stream()
                .flatMap(t -> transactionRepository
                        .findByMemberMemberIdOrderByBorrowDateDesc(t.getMember().getMemberId())
                        .stream())
                .map(t -> t.getBook().getIsbn())
                .distinct()
                .filter(i -> !i.equals(isbn))
                .limit(10)
                .collect(Collectors.toList());

        return bookRepository.findAllById(relatedIsbns).stream()
                .map(this::toResponseWithRating)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookResponse save(Book book) {
        Book saved = bookRepository.save(book);
        log.info("Book saved: {}", saved.getIsbn());
        return toResponseWithRating(saved);
    }

    @Transactional
    public void delete(String isbn) {
        if (!bookRepository.existsById(isbn)) {
            throw new ResourceNotFoundException("Book not found: " + isbn);
        }
        bookRepository.deleteById(isbn);
        log.info("Book deleted: {}", isbn);
    }

    @Transactional
    public Book updateStock(String isbn, int delta) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + isbn));
        int newStock = book.getStock() + delta;
        if (newStock < 0) throw new BadRequestException("Insufficient stock");
        book.setStock(newStock);
        if (delta < 0) {
            book.setAvailable(Math.max(0, book.getAvailable() + delta));
        } else {
            book.setAvailable(book.getAvailable() + delta);
        }
        return bookRepository.save(book);
    }

    private BookResponse toResponseWithRating(Book book) {
        double avgRating = reviewRepository.averageRatingByBookIsbn(book.getIsbn());
        long reviewCount = reviewRepository.countByBookIsbn(book.getIsbn());
        return BookResponse.from(book, avgRating, reviewCount);
    }
}
