package br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.Optional;

public interface IOrderRepository {
    Optional<Order> findById(OrderId orderId);

    OrderId save(Order order);
}
