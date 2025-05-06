package com.lms.controller;

import com.lms.dto.request.BookRequestDto;
import com.lms.dto.response.ApiResponse;
import com.lms.entity.BookRequest;
import com.lms.service.BookRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/book-requests")
public class BookRequestController {

    private final BookRequestService bookRequestService;

    public BookRequestController(BookRequestService bookRequestService) {
        this.bookRequestService = bookRequestService;
    }

    @GetMapping
    public ResponseEntity<List<BookRequest>> getMyRequests(
            @RequestAttribute("memberId") Integer memberId) {
        return ResponseEntity.ok(bookRequestService.getByMember(memberId));
    }

    @PostMapping
    public ResponseEntity<BookRequest> submitRequest(
            @RequestAttribute("memberId") Integer memberId,
            @Valid @RequestBody BookRequestDto dto) {
        return ResponseEntity.ok(bookRequestService.submit(memberId, dto));
    }
}
