package com.dat.backend.datshop.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowResponse {
    private Long id;
    private String followType;
    private Long userId;
    private Long targetId;
    private boolean isDeleted;
}
