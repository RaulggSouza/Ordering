package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.add_items;


import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderItemRequest;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record AddItemsToOrderItemRequestDTO(
        String productId,
        Integer quantity,
        Double price
) {
    public AddItemsToOrderItemRequest toApplicationRequest() {
        return new AddItemsToOrderItemRequest(
                new ProductId(productId),
                quantity,
                price
        );
    }
}