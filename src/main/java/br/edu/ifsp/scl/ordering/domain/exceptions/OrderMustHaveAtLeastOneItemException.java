package br.edu.ifsp.scl.ordering.domain.exceptions;

public class OrderMustHaveAtLeastOneItemException extends RuntimeException {
    public OrderMustHaveAtLeastOneItemException() {
        super("Order must have at least one item");
    }
}
