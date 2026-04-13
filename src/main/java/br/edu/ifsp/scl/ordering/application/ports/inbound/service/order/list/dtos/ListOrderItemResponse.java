package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.list.dtos;

import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record ListOrderItemResponse(ProductId productId, int quantity, double price){

    public static ListOrderItemResponse fromOrderItem(OrderItem orderItem) {
        return new ListOrderItemResponse(
                orderItem.productId(),
                orderItem.quantity(),
                orderItem.price()
        );
    }
}
