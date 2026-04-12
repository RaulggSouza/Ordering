package br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;

import java.util.List;


public record GetEligibleDiscountsResponse(List<GetEligibleDiscountsItemResponse> items) {

    public static GetEligibleDiscountsResponse createFromDiscounts(List<Discount> discounts, Order order) {
        return new GetEligibleDiscountsResponse(
                discounts.stream()
                        .map(discount -> new GetEligibleDiscountsItemResponse(
                                discount.getDiscountId(),
                                discount.getPercentage(order)
                        ))
                        .toList()
        );
    }

}
