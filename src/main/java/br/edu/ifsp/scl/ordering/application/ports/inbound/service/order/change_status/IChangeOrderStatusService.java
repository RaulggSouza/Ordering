package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusResponse;

public interface IChangeOrderStatusService {
    ChangeOrderStatusResponse change(ChangeOrderStatusRequest request);
}
