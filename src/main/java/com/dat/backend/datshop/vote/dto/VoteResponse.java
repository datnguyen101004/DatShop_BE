package com.dat.backend.datshop.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {
    private Long id;
    private Long userId;
    private Long targetId;
    private String voteType;
    private int voteValue;
    private String comment;
    private boolean isDeleted;
}
