package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.dtos.CancelOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;

import java.util.Optional;

public class CancelOrderService {
    private final IOrderRepository orderRepository;

    public CancelOrderService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean cancel(CancelOrderRequest request){
        Order order = orderRepository.findById(request.orderId()).get();

        if(order.ableToCancel()) order.cancelOrder();

        return order.ableToCancel();
    }
}
