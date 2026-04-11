package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos;

import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;

import java.util.List;

public record CreateOrderRequest(String customerId, Address address, List<CreateOrderItemRequest> items) {
}
