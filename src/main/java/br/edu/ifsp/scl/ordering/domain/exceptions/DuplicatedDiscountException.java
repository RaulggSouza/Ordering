package br.edu.ifsp.scl.ordering.domain.exceptions;

public class DuplicatedDiscountException extends RuntimeException {
    public DuplicatedDiscountException(String message) {
        super(message);
    }
}
