package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

public record CancelOrderRequest(OrderId orderId) {

}
