package com.dat.backend.datshop.follow.controller;

import com.dat.backend.datshop.follow.dto.FollowResponse;
import com.dat.backend.datshop.follow.dto.CreateFollow;
import com.dat.backend.datshop.follow.service.FollowService;
import com.dat.backend.datshop.template.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(
            summary = "Tạo một follow mới",
            description = "Tạo một follow mới cho người dùng hiện tại. Yêu cầu phải có xác thực."
    )
    @PostMapping("/create")
    public ApiResponse<FollowResponse> createFollow(@RequestBody CreateFollow createFollow, Authentication authentication) {
        return ApiResponse.success(followService.createFollow(createFollow, authentication.getName()));
    }

    @Operation(
            summary = "Lấy tất cả các follow của người dùng",
            description = "Lấy danh sách tất cả các follow của người dùng hiện tại. Yêu cầu phải có xác thực."
    )
    @GetMapping("/all")
    public ApiResponse<List<FollowResponse>> getAllFollows(Authentication authentication) {
        return ApiResponse.success(followService.getAllFollows(authentication.getName()));
    }
}
