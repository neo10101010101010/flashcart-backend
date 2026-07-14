package com.flashcart.flashcart_backend.exceptions;

public class UsuarioExistenteException extends RuntimeException{
    public UsuarioExistenteException(String message) {
        super(message);
    }
}
