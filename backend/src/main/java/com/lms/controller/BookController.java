package com.lms.controller;

import com.lms.dto.request.BookRequestDto;
import com.lms.dto.response.ApiResponse;
import com.lms.dto.response.BookResponse;
import com.lms.entity.Book;
import com.lms.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponse> getBook(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.findByIsbn(isbn));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String isbn) {
        return ResponseEntity.ok(bookService.search(title, author, genre, isbn));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<BookResponse>> getByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(bookService.findByGenre(genre));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<BookResponse>> getPopular() {
        return ResponseEntity.ok(bookService.getPopular());
    }

    @GetMapping("/{isbn}/recommendations")
    public ResponseEntity<List<BookResponse>> getRecommendations(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getRecommendations(isbn));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody Book book) {
        return ResponseEntity.ok(bookService.save(book));
    }

    @PutMapping("/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> updateBook(@PathVariable String isbn, @Valid @RequestBody Book book) {
        book.setIsbn(isbn);
        return ResponseEntity.ok(bookService.save(book));
    }

    @DeleteMapping("/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable String isbn) {
        bookService.delete(isbn);
        return ResponseEntity.ok(ApiResponse.ok("Book deleted"));
    }
}
