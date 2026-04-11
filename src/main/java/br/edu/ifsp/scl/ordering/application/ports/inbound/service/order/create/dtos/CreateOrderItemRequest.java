package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record CreateOrderItemRequest(ProductId productId, int quantity) {
}
