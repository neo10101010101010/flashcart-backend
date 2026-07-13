package com.flashcart.flashcart_backend.DTOS;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponse {
    private String token;
    private String username;
    private Long userId;

}
