package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.change_status;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusRequest;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.Locale;

public record ChangeOrderStatusRequestDTO(String newStatus) {
    public ChangeOrderStatusRequest toApplicationRequest(OrderId orderId) {
        return new ChangeOrderStatusRequest(
                orderId,
                OrderStatus.valueOf(newStatus.toUpperCase(Locale.ROOT))
        );
    }
}
