package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record AddItemsToOrderItemResponse (ProductId productId, int quantity, double price) { }
