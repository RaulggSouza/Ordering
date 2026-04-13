package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.remove_item;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderItemResponse;

public record RemoveItemFromOrderItemResponseDTO(
        String productId,
        Integer quantity,
        Double price
) {
    public static RemoveItemFromOrderItemResponseDTO fromApplicationResponse(
            RemoveItemFromOrderItemResponse response
    ) {
        return new RemoveItemFromOrderItemResponseDTO(
                response.productId().value(),
                response.quantity(),
                response.price()
        );
    }
}
