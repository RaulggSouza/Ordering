package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityResponse;

public interface IUpdateOrderItemQuantityService {
    UpdateOrderItemQuantityResponse updateOrderItemQuantity(UpdateOrderItemQuantityRequest request);
}
