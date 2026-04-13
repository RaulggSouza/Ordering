package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.remove_item;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderResponse;

import java.util.List;

public record RemoveItemFromOrderResponseDTO(
        String orderId,
        List<RemoveItemFromOrderItemResponseDTO> items
) {
    public static RemoveItemFromOrderResponseDTO fromApplicationResponse(
            RemoveItemFromOrderResponse response
    ) {
        return new RemoveItemFromOrderResponseDTO(
                response.orderId().value(),
                response.items().stream()
                        .map(RemoveItemFromOrderItemResponseDTO::fromApplicationResponse)
                        .toList()
        );
    }
}
