package com.lms.controller;

import com.lms.dto.request.BorrowRequest;
import com.lms.dto.request.ReturnRequest;
import com.lms.dto.response.ApiResponse;
import com.lms.dto.response.TransactionResponse;
import com.lms.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/my")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            @RequestAttribute("memberId") Integer memberId) {
        return ResponseEntity.ok(transactionService.getByMember(memberId));
    }

    @GetMapping("/my/active")
    public ResponseEntity<List<TransactionResponse>> getMyActiveLoans(
            @RequestAttribute("memberId") Integer memberId) {
        return ResponseEntity.ok(transactionService.getActiveByMember(memberId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponse>> getOverdue() {
        return ResponseEntity.ok(transactionService.getOverdue());
    }

    @PostMapping("/borrow")
    public ResponseEntity<TransactionResponse> borrowBook(
            @RequestAttribute("memberId") Integer memberId,
            @Valid @RequestBody BorrowRequest request) {
        return ResponseEntity.ok(transactionService.borrowBook(memberId, request.getIsbn()));
    }

    @PostMapping("/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> returnBook(@Valid @RequestBody ReturnRequest request) {
        return ResponseEntity.ok(transactionService.returnBook(request.getTransactionId(), request.getFine()));
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<TransactionResponse> renewBook(@PathVariable Integer id) {
        return ResponseEntity.ok(transactionService.renewBook(id));
    }

    @PostMapping("/{id}/pay-fine")
    public ResponseEntity<ApiResponse<Void>> payFine(@PathVariable Integer id) {
        transactionService.payFine(id);
        return ResponseEntity.ok(ApiResponse.ok("Fine paid successfully"));
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponse>> getByMember(@PathVariable Integer memberId) {
        return ResponseEntity.ok(transactionService.getByMember(memberId));
    }
}
