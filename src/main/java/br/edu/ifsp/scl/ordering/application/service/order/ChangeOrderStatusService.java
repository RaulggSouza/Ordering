package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.IChangeOrderStatusService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class ChangeOrderStatusService implements IChangeOrderStatusService {
    private final IOrderRepository orderRepository;

    public ChangeOrderStatusService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public ChangeOrderStatusResponse change(ChangeOrderStatusRequest request) {
        Order order = orderRepository.findById(request.orderId()).orElseThrow();
        OrderStatus previousStatus = order.getOrderStatus();
        if (request.newStatus() == OrderStatus.CANCELLED) {
            order.cancelOrder();
        } else {
            order.changeStatusTo(request.newStatus());
        }
        orderRepository.save(order);
        return new ChangeOrderStatusResponse(request.orderId(), previousStatus, request.newStatus());
    }
}
