package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

public record CreateOrderResponseDTO(String orderId) {
    public static CreateOrderResponseDTO toResponse(OrderId orderId){
        return new CreateOrderResponseDTO(orderId.value());
    }
}