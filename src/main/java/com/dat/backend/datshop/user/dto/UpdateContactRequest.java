package com.dat.backend.datshop.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateContactRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+][0-9 .-]{7,19}$", message = "Phone number is invalid")
    private String phoneNumber;

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address cannot exceed 255 characters")
    private String address;

    @NotBlank(message = "Ward is required")
    private String wardName;

    @NotBlank(message = "District is required")
    private String districtName;

    @NotBlank(message = "Province is required")
    private String provinceName;
}
