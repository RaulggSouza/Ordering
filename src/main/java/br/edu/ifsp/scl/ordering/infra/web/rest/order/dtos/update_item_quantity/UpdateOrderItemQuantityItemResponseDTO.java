package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.update_item_quantity;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityItemResponse;

public record UpdateOrderItemQuantityItemResponseDTO(
        String productId,
        Integer quantity,
        Double price
) {
    public static UpdateOrderItemQuantityItemResponseDTO fromApplicationResponse(
            UpdateOrderItemQuantityItemResponse response
    ) {
        return new UpdateOrderItemQuantityItemResponseDTO(
                response.productId().value(),
                response.quantity(),
                response.price()
        );
    }
}
