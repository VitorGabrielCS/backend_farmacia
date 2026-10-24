package org.example.backend_farmacia.exception;

public class InvalidResetCodeException extends RuntimeException {
    public InvalidResetCodeException() {
        super("Código inválido ou expirado");
    }
}
