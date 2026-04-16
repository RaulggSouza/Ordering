package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.IChangeOrderStatusService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class ChangeOrderStatusService implements IChangeOrderStatusService {
    private final IOrderRepository orderRepository;

    public ChangeOrderStatusService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public ChangeOrderStatusResponse change(ChangeOrderStatusRequest request) {
        return new ChangeOrderStatusResponse(null, null, null);
    }
}
