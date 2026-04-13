package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;


public record UpdateOrderItemQuantityRequest(
        OrderId orderId,
        ProductId productId,
        int quantity
) {
}
