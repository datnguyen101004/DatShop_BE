package com.dat.backend.datshop.vote.controller;

import com.dat.backend.datshop.template.ApiResponse;
import com.dat.backend.datshop.vote.dto.CreateVote;
import com.dat.backend.datshop.vote.dto.VoteResponse;
import com.dat.backend.datshop.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @Operation(
            summary = "Tạo một đánh giá mới hoặc update đánh giá hiện tại",
            description = "Tạo hoặc update một đánh giá mới cho sản phẩm hoặc cửa hàng. Yêu cầu phải có xác thực."
    )
    @PostMapping("/create")
    public ApiResponse<VoteResponse> createVote(@RequestBody CreateVote createVote, Authentication authentication) {
        return ApiResponse.success(voteService.createVote(createVote, authentication.getName()));
    }

    @Operation(
            summary = "Lấy tất cả đánh giá của người dùng",
            description = "Lấy danh sách tất cả đánh giá của người dùng hiện tại. Yêu cầu phải có xác thực."
    )
    @GetMapping("/all")
    public ApiResponse<List<VoteResponse>> getAllVotes(Authentication authentication) {
        return ApiResponse.success(voteService.getAllVotes(authentication.getName()));
    }

    @Operation(
            summary = "Xóa một đánh giá",
            description = "Xóa một đánh giá của người dùng hiện tại. Yêu cầu phải có xác thực."
    )
    @DeleteMapping("/delete/{voteId}")
    public ApiResponse<String> deleteVote(@PathVariable Long voteId, Authentication authentication) {
        return ApiResponse.success(voteService.deleteVote(voteId, authentication.getName()));
    }
}
