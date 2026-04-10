package br.edu.ifsp.scl.ordering.domain.entity;

import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record OrderItem(ProductId productId, int quantity, double value) {}
