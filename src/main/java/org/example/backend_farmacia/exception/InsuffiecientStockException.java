package org.example.backend_farmacia.exception;

public class InsuffiecientStockException extends RuntimeException {
    public InsuffiecientStockException(String message) {
        super(message);
    }
}
