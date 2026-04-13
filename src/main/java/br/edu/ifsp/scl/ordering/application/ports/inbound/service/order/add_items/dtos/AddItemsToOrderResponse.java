package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;

public record AddItemsToOrderResponse (OrderId orderId, List<AddItemsToOrderItemResponse> items) {}
