package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos;

import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

public record ChangeOrderStatusRequest(OrderId orderId, OrderStatus newStatus) {
}
