package com.dat.backend.datshop.user.controller;

import com.dat.backend.datshop.user.dto.UserResponse;
import com.dat.backend.datshop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    /**
     * Get profile information of the user.
     * @return UserResponse containing user profile information.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        log.info("Get profile for user: {}", authentication.getName());
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }
}
