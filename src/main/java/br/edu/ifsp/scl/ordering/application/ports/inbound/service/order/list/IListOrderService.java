package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.list;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.list.dtos.ListOrderResponse;

import java.util.List;

public interface IListOrderService {
    List<ListOrderResponse> listOrders();
}
