package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.change_status;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusResponse;

public record ChangeOrderStatusResponseDTO(
        String orderId,
        String previousStatus,
        String currentStatus
) {
    public static ChangeOrderStatusResponseDTO fromApplicationResponse(ChangeOrderStatusResponse response) {
        return new ChangeOrderStatusResponseDTO(
                response.orderId().value(),
                response.previousStatus().name(),
                response.currentStatus().name()
        );
    }
}