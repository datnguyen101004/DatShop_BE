package com.dat.backend.datshop.user.controller;

import com.dat.backend.datshop.user.dto.UserResponse;
import com.dat.backend.datshop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Get profile information of the user.
     * @param userId
     * @return UserResponse containing user profile information.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}
