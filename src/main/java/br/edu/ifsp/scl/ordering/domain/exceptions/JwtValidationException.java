package br.edu.ifsp.scl.ordering.domain.exceptions;

public class JwtValidationException extends RuntimeException {
    public JwtValidationException(String message) {
        super(message);
    }
}

