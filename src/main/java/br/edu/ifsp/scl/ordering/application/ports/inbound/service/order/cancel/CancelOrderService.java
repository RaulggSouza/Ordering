package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.dtos.CancelOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderNotFoundException;

import java.util.Objects;

public class CancelOrderService {
    private final IOrderRepository orderRepository;

    public CancelOrderService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean cancel(CancelOrderRequest request){
        Objects.requireNonNull(request.orderId());
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order with this id not found. Id: "+request.orderId()));

        order.cancelOrder();

        orderRepository.save(order);

        return true;
    }
}
