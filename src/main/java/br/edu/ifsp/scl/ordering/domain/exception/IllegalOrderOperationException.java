package br.edu.ifsp.scl.ordering.domain.exception;

public class IllegalOrderOperationException extends RuntimeException {
    public IllegalOrderOperationException(String message) {
        super(message);
    }
}
