package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.add_items;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderRequest;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;

public record AddItemsToOrderRequestDTO(List<AddItemsToOrderItemRequestDTO> items) {
    public AddItemsToOrderRequest toApplicationRequest(OrderId orderId) {
        return new AddItemsToOrderRequest(
                orderId,
                items.stream()
                        .map(AddItemsToOrderItemRequestDTO::toApplicationRequest)
                        .toList()
        );
    }
}
