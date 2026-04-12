package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos;

import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record AddItemsToOrderItemResponse (ProductId productId, int quantity, double price) {
    public static AddItemsToOrderItemResponse fromOrderItem(OrderItem orderItem) {
        return new AddItemsToOrderItemResponse(orderItem.productId(), orderItem.quantity(), orderItem.price());
    }
}
