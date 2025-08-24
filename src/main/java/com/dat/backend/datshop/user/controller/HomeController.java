package com.dat.backend.datshop.user.controller;

import com.dat.backend.datshop.template.ApiResponse;
import com.dat.backend.datshop.user.dto.HomeResponse;
import com.dat.backend.datshop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;

    @GetMapping("/")
    public ApiResponse<HomeResponse> home() {
        return ApiResponse.success(userService.getHome());
    }
}
