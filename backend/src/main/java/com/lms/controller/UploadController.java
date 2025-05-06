package com.lms.controller;

import com.lms.dto.response.ApiResponse;
import com.lms.dto.response.BookResponse;
import com.lms.entity.Book;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.BookRepository;
import com.lms.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@PreAuthorize("hasRole('ADMIN')")
public class UploadController {
    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    private final BookRepository bookRepository;
    private final BookService bookService;

    @Value("${app.upload.dir:uploads/covers}")
    private String uploadDir;

    public UploadController(BookRepository bookRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            log.error("Could not create upload directory", e);
        }
    }

    @PostMapping("/cover/{isbn}")
    public ResponseEntity<ApiResponse<BookResponse>> uploadCover(
            @PathVariable String isbn,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File is empty"));
        }

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + isbn));

        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, filename);
            Files.copy(file.getInputStream(), path);

            book.setCoverUrl("/uploads/covers/" + filename);
            bookRepository.save(book);

            log.info("Cover uploaded for book {}", isbn);
            return ResponseEntity.ok(ApiResponse.ok("Cover uploaded", BookResponse.from(book)));
        } catch (IOException e) {
            log.error("Failed to upload cover", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Upload failed"));
        }
    }
}
