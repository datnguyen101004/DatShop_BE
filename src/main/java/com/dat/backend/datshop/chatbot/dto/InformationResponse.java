package com.dat.backend.datshop.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformationResponse {
    private String id;
    private String name;
    private String description;
    private String type;
}
