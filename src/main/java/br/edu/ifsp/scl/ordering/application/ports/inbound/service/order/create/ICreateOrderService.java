package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderRequest;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

public interface ICreateOrderService {
    OrderId create(CreateOrderRequest request);
}
