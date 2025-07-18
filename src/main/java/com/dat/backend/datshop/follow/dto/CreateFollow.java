package com.dat.backend.datshop.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFollow {
    private Long targetId;

    @Schema(
            description = "Loại theo dõi, có thể là 'product' hoặc 'shop'",
            example = "product"
    )
    private String followType;
}
