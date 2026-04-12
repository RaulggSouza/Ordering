package br.edu.ifsp.scl.ordering.domain.exceptions;

public class EmptyOrderItemListException extends RuntimeException {
    public EmptyOrderItemListException(String message) {
        super(message);
    }
}
