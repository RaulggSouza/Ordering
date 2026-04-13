package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;

public record AddItemsToOrderRequest(OrderId orderId, List<AddItemsToOrderItemRequest> addItemsToOrderItemRequest) { }
