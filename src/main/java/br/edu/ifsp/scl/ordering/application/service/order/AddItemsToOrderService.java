package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.IAddItemsToOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderItemRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderItemResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

import java.util.ArrayList;
import java.util.List;

public class AddItemsToOrderService implements IAddItemsToOrderService {
    private final IOrderRepository orderRepository;
    private final IProductRepository  productRepository;

    public AddItemsToOrderService(IOrderRepository orderRepository,  IProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public AddItemsToOrderResponse addItemsToOrder(AddItemsToOrderRequest request) {
        Order order = orderRepository.findById(request.orderId()).get();

        productRepository.allExistsByIds(request.addItemsToOrderItemRequest().stream().map((AddItemsToOrderItemRequest::productId)).toList());

        orderRepository.save(order);

        return new AddItemsToOrderResponse(new OrderId("1"),
                List.of(new AddItemsToOrderItemResponse(new ProductId("1"), 1, 100)));
    }
}
