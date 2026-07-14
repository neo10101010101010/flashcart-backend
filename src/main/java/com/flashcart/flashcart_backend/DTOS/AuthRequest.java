package com.flashcart.flashcart_backend.DTOS;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthRequest {
    @Schema(
            description = "username del usuario",
            example = "neo"
    )
    private String username;
    @Schema(
            description = "contraseña del usuario",
            example = "Contr@señ@1"
    )
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
