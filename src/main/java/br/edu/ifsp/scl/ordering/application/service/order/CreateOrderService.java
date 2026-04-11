package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.ICreateOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.customer.ICustomerRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.inventory.IProductInventoryRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.CustomerNotFoundException;
import br.edu.ifsp.scl.ordering.domain.exceptions.EmptyOrderItemListException;
import br.edu.ifsp.scl.ordering.domain.exceptions.ProductNotFoundException;
import br.edu.ifsp.scl.ordering.domain.exceptions.ProductOutOfStockException;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

import java.util.List;
import java.util.Objects;

public class CreateOrderService implements ICreateOrderService {
    ICustomerRepository customerRepository;
    IProductRepository productRepository;
    IOrderRepository orderRepository;
    IProductInventoryRepository productInventoryRepository;

    public CreateOrderService(ICustomerRepository customerRepository, IProductRepository productRepository, IOrderRepository orderRepository, IProductInventoryRepository productInventoryRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.productInventoryRepository = productInventoryRepository;
    }

    @Override
    public OrderId create(CreateOrderRequest request) {
        Objects.requireNonNull(request, "Null request");
        Objects.requireNonNull(request.customerId(), "Customer must not be null");
        Objects.requireNonNull(request.items(), "OrderItems list must not be null");
        if (request.items().isEmpty()) throw new EmptyOrderItemListException("Order items list must not be empty");
        List<OrderItem> items = getOrderItems(request);

        customerRepository.findById(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found. Id: "+request.customerId()));
        boolean allExistsByIds = productRepository.allExistsByIds(items.stream()
                .map(OrderItem::productId)
                .toList());
        if(!allExistsByIds) throw new ProductNotFoundException("Products does not exists");

        List<ProductId> outOfStockItems = productInventoryRepository.findOutOfStockItems(items.stream()
                .map(OrderItem::productId)
                .toList());
        if (!outOfStockItems.isEmpty()) throw new ProductOutOfStockException("Products out of stock. Products: "+outOfStockItems);

        Order order = Order.create(items, request.address(), request.customerId());

        return orderRepository.save(order);
    }

    private static List<OrderItem> getOrderItems(CreateOrderRequest request) {
        request.items().forEach(item -> {
            Objects.requireNonNull(item, "OrderItems list item must not be null");
            if(item.quantity() < 1) throw new IllegalArgumentException("Quantity of an Item mus not be less than one");
        });

        return request.items().stream()
                .map(item -> new OrderItem(
                        item.productId(),
                        item.quantity()
                ))
                .toList();
    }
}
