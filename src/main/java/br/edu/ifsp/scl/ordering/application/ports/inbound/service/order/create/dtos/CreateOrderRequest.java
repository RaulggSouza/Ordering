package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;

import java.util.List;

public record CreateOrderRequest(CustomerId customerId, Address address, List<CreateOrderItemRequest> items) {
}
