package br.edu.ifsp.scl.ordering.application.service.discount;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.exception.IllegalOrderOperationException;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;
import java.util.Optional;

public class ApplyDiscountService {
    private final IOrderRepository orderRepository;
    private final IDiscountRepository discountRepository;

    public ApplyDiscountService(IOrderRepository orderRepository, IDiscountRepository discountRepository) {
        this.orderRepository = orderRepository;
        this.discountRepository = discountRepository;
    }

    public void apply(OrderId orderId, List<DiscountId> discountIds) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new IllegalOrderOperationException("Cannot apply discount for cancelled order \"%s\"!"
                    .formatted(orderId)
            );

        discountIds.stream()
                .map(discountRepository::findById)
                .flatMap(Optional::stream)
                .forEach(order::addDiscount);
    }
}
