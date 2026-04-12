package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.order;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import org.springframework.stereotype.Repository;

@Repository
public class OrderPostgresqlRepository implements IOrderRepository {
    private final SpringDataOrderRepository repository;

    public OrderPostgresqlRepository(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrderId save(Order order) {
        OrderEntity saved = repository.save(toEntity(order));
        return new OrderId(saved.getId());
    }

    private OrderEntity toEntity(Order order){
        return new OrderEntity(
                order.getId().value(),
                order.getCustomerId().value(),
                order.getShippingAddress().street(),
                order.getShippingAddress().number(),
                order.getShippingAddress().city(),
                order.getShippingAddress().state(),
                order.getShippingAddress().postalCode(),
                order.getStatus()
        );
    }
}
