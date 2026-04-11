package br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

public interface IOrderRepository {
    OrderId save(Order order);
}
