package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderItemRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.IUpdateOrderItemQuantityService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderNotFoundException;
import br.edu.ifsp.scl.ordering.domain.exceptions.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateOrderItemQuantityService implements IUpdateOrderItemQuantityService {
    private final IOrderRepository orderRepository;
    private final IProductRepository productRepository;

    public UpdateOrderItemQuantityService(IOrderRepository orderRepository, IProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public UpdateOrderItemQuantityResponse updateOrderItemQuantity(UpdateOrderItemQuantityRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new OrderNotFoundException(request.orderId()));

        if(!productRepository.existsById(request.productId())){
            throw new ProductNotFoundException("Product not found");
        }

        order.updateItemQuantity(request.productId(), request.quantity());

        orderRepository.save(order);

        return UpdateOrderItemQuantityResponse.createFromOrder(order);
    }
}
