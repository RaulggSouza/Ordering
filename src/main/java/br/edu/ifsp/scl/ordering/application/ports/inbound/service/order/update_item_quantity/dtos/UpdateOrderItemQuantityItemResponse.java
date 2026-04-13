package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos;

import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record UpdateOrderItemQuantityItemResponse(
        ProductId productId,
        int quantity,
        double price
) {
    public static UpdateOrderItemQuantityItemResponse fromOrderItem(OrderItem orderItem) {
        return new UpdateOrderItemQuantityItemResponse(
                orderItem.productId(),
                orderItem.quantity(),
                orderItem.price()
        );
    }
}