package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.update_item_quantity;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityResponse;

import java.util.List;

public record UpdateOrderItemQuantityResponseDTO(
        String orderId,
        List<UpdateOrderItemQuantityItemResponseDTO> items
) {
    public static UpdateOrderItemQuantityResponseDTO fromApplicationResponse(
            UpdateOrderItemQuantityResponse response
    ) {
        return new UpdateOrderItemQuantityResponseDTO(
                response.orderId().value(),
                response.items().stream()
                        .map(UpdateOrderItemQuantityItemResponseDTO::fromApplicationResponse)
                        .toList()
        );
    }
}
