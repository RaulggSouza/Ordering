package br.edu.ifsp.scl.ordering.domain.exceptions;

import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

import java.util.List;

public class InvalidOrderItemQuantityException extends RuntimeException {
    public InvalidOrderItemQuantityException(List<ProductId> productIds) {
        super("Item quantity must be greater than zero for products: " +
                String.join(", ", productIds.stream().map(ProductId::value).toList()));
    }
}