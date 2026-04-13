package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.list_orders;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.list.dtos.ListOrderResponse;

import java.util.List;

public record ListOrdersOrderResponseDTO(
        String orderId,
        String status,
        List<ListOrdersItemResponseDTO> items,
        List<ListOrdersDiscountResponseDTO> discounts
) {
    public static ListOrdersOrderResponseDTO fromApplicationResponse(ListOrderResponse response) {
        return new ListOrdersOrderResponseDTO(
                response.orderId().value(),
                response.status().name(),
                response.items().stream()
                        .map(item -> new ListOrdersItemResponseDTO(
                                item.productId().value(),
                                item.quantity(),
                                item.price()
                        ))
                        .toList(),
                response.discounts().stream()
                        .map(discount -> new ListOrdersDiscountResponseDTO(
                                discount.discountId().value(),
                                discount.percentage(),
                                discount.discountType().name()
                        ))
                        .toList()
        );
    }
}

