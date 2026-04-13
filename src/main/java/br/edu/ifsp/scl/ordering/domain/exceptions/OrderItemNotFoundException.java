package br.edu.ifsp.scl.ordering.domain.exceptions;

import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public class OrderItemNotFoundException extends RuntimeException {
    public OrderItemNotFoundException(ProductId productId) {
        super("Product with id '" + productId.value() + "' was not found in order");
    }
}
