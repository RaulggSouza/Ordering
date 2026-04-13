package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderResponse;

public interface IRemoveItemFromOrderService {
    RemoveItemFromOrderResponse removeItemFromOrder(RemoveItemFromOrderRequest request);
}
