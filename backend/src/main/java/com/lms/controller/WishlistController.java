package com.lms.controller;

import com.lms.dto.request.BorrowRequest;
import com.lms.dto.response.ApiResponse;
import com.lms.dto.response.BookResponse;
import com.lms.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getWishlist(
            @RequestAttribute("memberId") Integer memberId) {
        return ResponseEntity.ok(wishlistService.getByMember(memberId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addToWishlist(
            @RequestAttribute("memberId") Integer memberId,
            @Valid @RequestBody BorrowRequest request) {
        wishlistService.add(memberId, request.getIsbn());
        return ResponseEntity.ok(ApiResponse.ok("Added to wishlist"));
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @RequestAttribute("memberId") Integer memberId,
            @PathVariable String isbn) {
        wishlistService.remove(memberId, isbn);
        return ResponseEntity.ok(ApiResponse.ok("Removed from wishlist"));
    }
}
