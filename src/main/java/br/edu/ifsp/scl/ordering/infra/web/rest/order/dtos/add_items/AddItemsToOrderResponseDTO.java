package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.add_items;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderResponse;

import java.util.List;

public record AddItemsToOrderResponseDTO(
        String orderId,
        List<AddItemsToOrderItemResponseDTO> items
) {
    public static AddItemsToOrderResponseDTO fromApplicationResponse(AddItemsToOrderResponse response) {
        return new AddItemsToOrderResponseDTO(
                response.orderId().value(),
                response.items().stream()
                        .map(item -> new AddItemsToOrderItemResponseDTO(
                                item.productId().value(),
                                item.quantity(),
                                item.price()
                        ))
                        .toList()
        );
    }
}
