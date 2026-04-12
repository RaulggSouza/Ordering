package br.edu.ifsp.scl.ordering.domain.exceptions;

public class ProductOutOfStockException extends RuntimeException {
    public ProductOutOfStockException(String message) {
        super(message);
    }
}
