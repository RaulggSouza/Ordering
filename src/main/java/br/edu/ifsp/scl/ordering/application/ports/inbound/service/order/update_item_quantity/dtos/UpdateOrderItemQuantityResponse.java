package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;

public record UpdateOrderItemQuantityResponse(
        OrderId orderId,
        List<UpdateOrderItemQuantityItemResponse> items
) {
    public static UpdateOrderItemQuantityResponse createFromOrder(Order order) {
        return new UpdateOrderItemQuantityResponse(order.getOrderId(), order.getItems().stream().map(UpdateOrderItemQuantityItemResponse::fromOrderItem).toList());
    }
}