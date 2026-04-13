package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.list_orders;

public record ListOrdersItemResponseDTO(
        String productId,
        int quantity,
        double price
) {
}

