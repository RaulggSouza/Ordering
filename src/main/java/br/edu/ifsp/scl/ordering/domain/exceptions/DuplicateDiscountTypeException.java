package br.edu.ifsp.scl.ordering.domain.exceptions;

public class DuplicateDiscountTypeException extends RuntimeException {
    public DuplicateDiscountTypeException(String message) {
        super(message);
    }
}
