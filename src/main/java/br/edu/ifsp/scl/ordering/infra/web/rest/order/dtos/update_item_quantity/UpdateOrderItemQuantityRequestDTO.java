package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.update_item_quantity;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityRequest;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record UpdateOrderItemQuantityRequestDTO(Integer quantity) {

    public UpdateOrderItemQuantityRequest toApplicationRequest(OrderId orderId, ProductId productId) {
        return new UpdateOrderItemQuantityRequest(
                orderId,
                productId,
                quantity
        );
    }
}
