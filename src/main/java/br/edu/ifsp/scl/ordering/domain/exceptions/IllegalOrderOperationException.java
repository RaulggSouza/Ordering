package br.edu.ifsp.scl.ordering.domain.exceptions;

public class IllegalOrderOperationException extends RuntimeException {
    public IllegalOrderOperationException(String message) {
        super(message);
    }
}
