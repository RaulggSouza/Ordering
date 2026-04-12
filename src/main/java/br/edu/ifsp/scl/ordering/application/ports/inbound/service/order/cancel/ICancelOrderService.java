package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.dtos.CancelOrderRequest;

public interface ICancelOrderService {
    boolean cancel(CancelOrderRequest request);
}
