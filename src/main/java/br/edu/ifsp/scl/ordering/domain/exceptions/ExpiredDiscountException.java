package br.edu.ifsp.scl.ordering.domain.exceptions;

public class ExpiredDiscountException extends RuntimeException {
    public ExpiredDiscountException(String message) {
        super(message);
    }
}
