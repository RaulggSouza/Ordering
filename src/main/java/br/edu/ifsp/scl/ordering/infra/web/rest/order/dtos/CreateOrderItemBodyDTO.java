package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderItemRequest;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

public record CreateOrderItemBodyDTO(String productId, int quantity, double price) {
    public CreateOrderItemRequest toRequest(){
        return new CreateOrderItemRequest(new ProductId(productId), quantity, price);
    }
}
