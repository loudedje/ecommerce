package com.compassuol.sp.challenge.ecommerce.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponseDTO {
    private final String token;
    private final String type = "Bearer";

    public LoginResponseDTO(String token) {
        this.token = token;
    }
}
