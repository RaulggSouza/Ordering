package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderRequest;

import java.util.UUID;

public interface ICreateOrderService {
    UUID create(CreateOrderRequest request);
}
