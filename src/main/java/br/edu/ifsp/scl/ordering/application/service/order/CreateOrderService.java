package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.ICreateOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.customer.ICustomerRepository;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

import java.util.List;
import java.util.UUID;

public class CreateOrderService implements ICreateOrderService {
    ICustomerRepository customerRepository;

    public CreateOrderService(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UUID create(CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream()
                .map(item -> new OrderItem(
                        new ProductId(item.productId()),
                        item.quantity()
                ))
                .toList();


        customerRepository.findById(new CustomerId(request.customerId()));
        return UUID.randomUUID();
    }
}
