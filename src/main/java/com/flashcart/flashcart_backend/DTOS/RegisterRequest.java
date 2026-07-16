package com.flashcart.flashcart_backend.DTOS;

import io.swagger.v3.oas.annotations.media.Schema;

public class RegisterRequest {
    @Schema(hidden = true)
    private Long id;
    @Schema(
            description = "username del usuario",
            example = "neo"
    )
    private String username;
    @Schema(
            description = "correo electronico del usuario",
            example = "neo@correo.com"
    )
    private String email;
    @Schema(
            description = "contraseña del usuario",
            example = "Contr@señ@1"
    )
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
