package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderResponse;

public interface IAddItemsToOrderService {
    AddItemsToOrderResponse addItemsToOrder(AddItemsToOrderRequest request);
}
