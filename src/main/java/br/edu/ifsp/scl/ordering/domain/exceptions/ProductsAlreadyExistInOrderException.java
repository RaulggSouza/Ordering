package br.edu.ifsp.scl.ordering.domain.exceptions;

import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

import java.util.List;

public class ProductsAlreadyExistInOrderException extends RuntimeException {
    public ProductsAlreadyExistInOrderException(List<ProductId> productIds) {
        super("Products already exist in order: " +
                productIds.stream()
                        .map(ProductId::value)
                        .toList());
    }
}