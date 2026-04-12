package br.edu.ifsp.scl.ordering.application.service.discount;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.IGetEligibleDiscountsService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GetEligibleDiscountsService implements IGetEligibleDiscountsService {
    IOrderRepository orderRepository;
    IDiscountRepository discountRepository;

    @Override
    public GetEligibleDiscountsResponse getEligibleDiscounts(GetEligibleDiscountsRequest request) {
        Optional<Order> order = orderRepository.findById(request.orderId());

        List<Discount> discounts  = discountRepository.getAll();

        List<Discount> evaluatedDiscounts = discounts.stream().filter((discount) -> discount.isEligible(order.get())).toList();

        return new GetEligibleDiscountsResponse(evaluatedDiscounts);
    }
}
