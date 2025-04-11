package com.lms.controller;

import com.lms.dto.response.ApiResponse;
import com.lms.dto.response.MemberResponse;
import com.lms.entity.AuditLog;
import com.lms.entity.BookRequest;
import com.lms.entity.BorrowingLimit;
import com.lms.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getMembers(page, size));
    }

    @GetMapping("/members/count")
    public ResponseEntity<Long> getMemberCount() {
        return ResponseEntity.ok(adminService.getMemberCount());
    }

    @PostMapping("/members/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleMemberStatus(@PathVariable Integer id) {
        adminService.toggleMemberStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Member status updated"));
    }

    @GetMapping("/book-requests")
    public ResponseEntity<List<BookRequest>> getBookRequests(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getBookRequests(status));
    }

    @PostMapping("/book-requests/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveRequest(@PathVariable Integer id) {
        adminService.approveBookRequest(id);
        return ResponseEntity.ok(ApiResponse.ok("Request approved"));
    }

    @PostMapping("/book-requests/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectRequest(@PathVariable Integer id) {
        adminService.rejectBookRequest(id);
        return ResponseEntity.ok(ApiResponse.ok("Request rejected"));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getAuditLogs(page, size));
    }

    @GetMapping("/audit-logs/count")
    public ResponseEntity<Long> getAuditLogCount() {
        return ResponseEntity.ok(adminService.getAuditLogCount());
    }

    @GetMapping("/borrowing-limits")
    public ResponseEntity<List<BorrowingLimit>> getBorrowingLimits() {
        return ResponseEntity.ok(adminService.getBorrowingLimits());
    }

    @PutMapping("/borrowing-limits/{id}")
    public ResponseEntity<BorrowingLimit> updateBorrowingLimit(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer maxBooks,
            @RequestParam(required = false) Integer loanDurationDays) {
        return ResponseEntity.ok(adminService.updateBorrowingLimit(id, maxBooks, loanDurationDays));
    }

    @GetMapping("/popular-books")
    public ResponseEntity<List<Map<String, Object>>> getPopularBooks() {
        return ResponseEntity.ok(adminService.getPopularBooks());
    }
}
