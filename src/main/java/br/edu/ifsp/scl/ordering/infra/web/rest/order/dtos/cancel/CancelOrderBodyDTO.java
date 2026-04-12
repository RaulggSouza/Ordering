package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.cancel;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.dtos.CancelOrderRequest;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

public record CancelOrderBodyDTO(String orderId) {

    public CancelOrderRequest toRequest(){
        return new CancelOrderRequest(new OrderId(orderId));
    }
}
