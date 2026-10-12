package com.gaminghub.controller;

import com.gaminghub.dto.UserProfileUpdateRequest;
import com.gaminghub.dto.UserResponse;
import com.gaminghub.entity.User;
import com.gaminghub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getCurrentUserResponse());
    }

    @PatchMapping("/users/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@AuthenticationPrincipal User currentUser,
                                                        @Valid @RequestBody UserProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateCurrentUser(
                currentUser,
                request.getFullName(),
                request.getEmail(),
                request.getCity(),
                request.getProfileImageUrl()));
    }
}
