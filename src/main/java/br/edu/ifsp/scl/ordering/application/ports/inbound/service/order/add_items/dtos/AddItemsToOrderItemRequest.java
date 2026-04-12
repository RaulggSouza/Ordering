package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos;

import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record AddItemsToOrderItemRequest(ProductId productId, int quantity, double price) {
    public OrderItem toOrderItem(){
        return new OrderItem(productId, quantity, price);
    }
}
