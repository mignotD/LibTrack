package com.lms.controller;

import com.lms.dto.request.ChangePasswordRequest;
import com.lms.dto.response.ApiResponse;
import com.lms.dto.response.MemberResponse;
import com.lms.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<MemberResponse> getProfile(@RequestAttribute("memberId") Integer memberId) {
        return ResponseEntity.ok(profileService.getProfile(memberId));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestAttribute("memberId") Integer memberId,
            @Valid @RequestBody ChangePasswordRequest request) {
        profileService.changePassword(memberId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully"));
    }

    @PostMapping("/update-email")
    public ResponseEntity<ApiResponse<Void>> updateEmail(
            @RequestAttribute("memberId") Integer memberId,
            @RequestBody String newEmail) {
        profileService.updateEmail(memberId, newEmail);
        return ResponseEntity.ok(ApiResponse.ok("Email updated successfully"));
    }
}
