package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.ICreateOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderItemRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.customer.ICustomerRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

import java.util.List;
import java.util.UUID;

public class CreateOrderService implements ICreateOrderService {
    ICustomerRepository customerRepository;
    IProductRepository productRepository;

    public CreateOrderService(ICustomerRepository customerRepository, IProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public UUID create(CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream()
                .map(item -> new OrderItem(
                        item.productId(),
                        item.quantity()
                ))
                .toList();


        customerRepository.findById(request.customerId());
        productRepository.allExistsByIds(request.items().stream()
                .map(CreateOrderItemRequest::productId)
                .toList());
        return UUID.randomUUID();
    }
}
