package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.list_orders;

public record ListOrdersDiscountResponseDTO(
        String discountId,
        double percentage,
        String discountType
) {
}

