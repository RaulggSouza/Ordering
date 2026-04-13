package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos;

import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record RemoveItemFromOrderItemResponse(
        ProductId productId,
        int quantity,
        double price
) {
    public static RemoveItemFromOrderItemResponse fromOrderItem(OrderItem orderItem) {
        return new RemoveItemFromOrderItemResponse(
                orderItem.productId(),
                orderItem.quantity(),
                orderItem.price()
        );
    }
}
