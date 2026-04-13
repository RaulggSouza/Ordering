package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;

public record RemoveItemFromOrderResponse(
        OrderId orderId,
        List<RemoveItemFromOrderItemResponse> items
) {
    public static RemoveItemFromOrderResponse createFromOrder(Order order) {
        return new RemoveItemFromOrderResponse(order.getOrderId(), order.getItems().stream().map(RemoveItemFromOrderItemResponse::fromOrderItem).toList());
    }
}
