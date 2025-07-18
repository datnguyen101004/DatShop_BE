package com.dat.backend.datshop.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVote {
    private Long targetId;
    @Schema(
            description = "loại bình chọn, có thể là 'product', 'shop', 'delivery'",
            example = "product"
    )
    private String voteType;
    private String comment;
    private int voteValue;
}
