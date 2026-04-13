package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record RemoveItemFromOrderRequest(
        OrderId orderId,
        ProductId productId
) {
}
