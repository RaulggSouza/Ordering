package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.ICreateOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.customer.ICustomerRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.CustomerNotFoundException;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;
import java.util.Objects;

public class CreateOrderService implements ICreateOrderService {
    ICustomerRepository customerRepository;
    IProductRepository productRepository;
    IOrderRepository orderRepository;

    public CreateOrderService(ICustomerRepository customerRepository, IProductRepository productRepository, IOrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderId create(CreateOrderRequest request) {
        Objects.requireNonNull(request, "Null request");
        Objects.requireNonNull(request.customerId(), "Customer must not be null");

        List<OrderItem> items = request.items().stream()
                .map(item -> new OrderItem(
                        item.productId(),
                        item.quantity()
                ))
                .toList();
        customerRepository.findById(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found. Id: "+request.customerId()));
        productRepository.allExistsByIds(items.stream()
                .map(OrderItem::productId)
                .toList());

        Order order = Order.create(items, request.address(), request.customerId());

        return orderRepository.save(order);
    }
}
