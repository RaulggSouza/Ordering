package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.IAddItemsToOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderItemRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderItemResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderNotFoundException;
import br.edu.ifsp.scl.ordering.domain.exceptions.ProductNotFoundException;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AddItemsToOrderService implements IAddItemsToOrderService {
    private final IOrderRepository orderRepository;
    private final IProductRepository  productRepository;

    public AddItemsToOrderService(IOrderRepository orderRepository,  IProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public AddItemsToOrderResponse addItemsToOrder(AddItemsToOrderRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new OrderNotFoundException(request.orderId()));

        List<AddItemsToOrderItemRequest> itemsToAddRequest = request.addItemsToOrderItemRequest();

        if (itemsToAddRequest == null || itemsToAddRequest.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }

        List<ProductId> productIds = itemsToAddRequest.stream()
                .map(AddItemsToOrderItemRequest::productId)
                .toList();

        if (!productRepository.allExistsByIds(productIds)) {
            throw new ProductNotFoundException("Product not found");
        }

        List<OrderItem> itemsToAdd = itemsToAddRequest.stream()
                .map(item -> new OrderItem(item.productId(), item.quantity(), item.price()))
                .toList();

        order.addItems(itemsToAdd);

        orderRepository.save(order);

        return AddItemsToOrderResponse.from(order);
    }
}
