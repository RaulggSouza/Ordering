package br.edu.ifsp.scl.ordering.application.service.discount;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.IGetEligibleDiscountsService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderNotFoundException;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderStatusNotAllowedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEligibleDiscountsService implements IGetEligibleDiscountsService {
    IOrderRepository orderRepository;
    IDiscountRepository discountRepository;

    public GetEligibleDiscountsService(IOrderRepository orderRepository, IDiscountRepository discountRepository) {
        this.orderRepository = orderRepository;
        this.discountRepository = discountRepository;
    }


    @Override
    public GetEligibleDiscountsResponse getEligibleDiscounts(GetEligibleDiscountsRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new OrderNotFoundException(request.orderId()));

        validateOrderStatus(order);

        List<Discount> eligibleDiscounts = discountRepository.getAll().stream()
                .filter(discount -> discount.isEligible(order))
                .toList();

        return GetEligibleDiscountsResponse.createFromDiscounts(eligibleDiscounts, order);
    }

    private void validateOrderStatus(Order order) {
        if (!order.getOrderStatus().allowsGettingEligibleDiscounts()) {
            throw new OrderStatusNotAllowedException(order.getOrderStatus());
        }
    }
}
