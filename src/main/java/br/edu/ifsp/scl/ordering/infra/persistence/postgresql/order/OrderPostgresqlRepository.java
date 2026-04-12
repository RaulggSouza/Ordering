package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.order;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderPostgresqlRepository implements IOrderRepository {
    private final SpringDataOrderRepository repository;

    public OrderPostgresqlRepository(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return repository.findById(orderId.value())
                .map(this::toDomain);
    }

    @Override
    public OrderId save(Order order) {
        OrderEntity saved = repository.save(toEntity(order));
        return new OrderId(saved.getId());
    }

    private Order toDomain(OrderEntity orderEntity) {
        List<OrderItem> items = orderEntity.getItems().stream()
                .map(this::toDomain)
                .toList();

        Address shippingAddress = new Address(
                orderEntity.getStreet(),
                orderEntity.getNumber(),
                orderEntity.getCity(),
                orderEntity.getState(),
                orderEntity.getPostalCode()
        );

        return new Order(
                new OrderId(orderEntity.getId()),
                items,
                List.of(),
                orderEntity.getStatus(),
                new CustomerId(orderEntity.getCustomerId()),
                shippingAddress
        );
    }

    private OrderItem toDomain(OrderItemEntity orderItemEntity) {
        return new OrderItem(
                new ProductId(orderItemEntity.getProductId()),
                orderItemEntity.getQuantity(),
                orderItemEntity.getPrice()
        );
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity(
                order.getOrderId().value(),
                order.getCustomerId().value(),
                order.getShippingAddress().street(),
                order.getShippingAddress().number(),
                order.getShippingAddress().city(),
                order.getShippingAddress().state(),
                order.getShippingAddress().postalCode(),
                order.getOrderStatus()
        );

        order.getItems().forEach(item -> orderEntity.addItem(toEntity(item)));

        return orderEntity;
    }


    private OrderItemEntity toEntity(OrderItem orderItem) {
        return new OrderItemEntity(
                orderItem.productId().value(),
                orderItem.quantity(),
                orderItem.price()
        );
    }
}
