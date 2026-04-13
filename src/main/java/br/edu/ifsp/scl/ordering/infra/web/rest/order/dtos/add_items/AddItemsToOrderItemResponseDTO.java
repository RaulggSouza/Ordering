package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.add_items;

public record AddItemsToOrderItemResponseDTO(
        String productId,
        Integer quantity,
        Double price
) {
}
