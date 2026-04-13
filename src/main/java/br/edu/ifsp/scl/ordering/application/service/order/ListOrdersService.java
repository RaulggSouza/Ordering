package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.list.IListOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.list.dtos.ListOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListOrdersService implements IListOrderService {
    private final IOrderRepository orderRepository;

    public ListOrdersService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<ListOrderResponse> listOrders() {
        return orderRepository.findAll().stream()
                .map(ListOrderResponse::fromOrder)
                .toList();
    }
}

