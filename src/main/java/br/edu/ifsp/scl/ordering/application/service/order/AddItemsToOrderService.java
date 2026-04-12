package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.IAddItemsToOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderItemRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderItemResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.exceptions.EmptyOrderItemListException;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderNotFoundException;
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
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new OrderNotFoundException(request.orderId()));

        if(!productRepository.allExistsByIds(request.addItemsToOrderItemRequest().stream().map((AddItemsToOrderItemRequest::productId)).toList())){
            throw new EmptyOrderItemListException("Product not found");
        }

        order.addItems(request.addItemsToOrderItemRequest().stream().map(AddItemsToOrderItemRequest::toOrderItem).toList());

        orderRepository.save(order);

        return new AddItemsToOrderResponse(
                order.getOrderId(),
                order.getItems().stream()
                        .map(AddItemsToOrderItemResponse::fromOrderItem)
                        .toList()
        );
    }
}
