package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.IRemoveItemFromOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderNotFoundException;
import br.edu.ifsp.scl.ordering.domain.exceptions.ProductNotFoundException;

public class RemoveItemFromOrderService implements IRemoveItemFromOrderService {
    private final IOrderRepository orderRepository;
    private final IProductRepository productRepository;

    public RemoveItemFromOrderService(IOrderRepository orderRepository, IProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public RemoveItemFromOrderResponse removeItemFromOrder(RemoveItemFromOrderRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new OrderNotFoundException(request.orderId()));

        if(!productRepository.existsById(request.productId())){
            throw new ProductNotFoundException("Product not found");
        }

        order.removeItem(request.productId());

        orderRepository.save(order);

        return RemoveItemFromOrderResponse.createFromOrder(order);
    }
}
