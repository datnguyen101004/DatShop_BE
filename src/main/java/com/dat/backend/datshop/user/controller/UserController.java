package com.dat.backend.datshop.user.controller;

import com.dat.backend.datshop.user.dto.UserResponse;
import com.dat.backend.datshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @Operation(
            summary = "Lấy thông tin người dùng",
            description = "Không cần truyền tham số, sử dụng token để xác thực người dùng."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Thành công."),
                    @ApiResponse(responseCode = "401", description = "Thất bại vì người dùng chưa đăng nhập hoặc không có quyền truy cập."),
                    @ApiResponse(responseCode = "403", description = "Thất bại vì người dùng không có quyền truy cập vào tài nguyên này."),
            }
    )
    @GetMapping("/profile")
    public com.dat.backend.datshop.response.ApiResponse<UserResponse> getProfile(Authentication authentication) {
        log.info("Get profile for user: {}", authentication.getName());
        return com.dat.backend.datshop.response.ApiResponse.success(userService.getProfile(authentication.getName()));
    }
}
