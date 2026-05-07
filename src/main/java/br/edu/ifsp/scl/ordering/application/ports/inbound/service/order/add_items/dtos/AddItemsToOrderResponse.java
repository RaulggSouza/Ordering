package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;

public record AddItemsToOrderResponse(
        OrderId orderId,
        List<AddItemsToOrderItemResponse> items
) {

    public static AddItemsToOrderResponse from(Order order) {
        return new AddItemsToOrderResponse(
                order.getOrderId(),
                order.getItems()
                        .stream()
                        .map(AddItemsToOrderItemResponse::fromOrderItem)
                        .toList()
        );
    }
}