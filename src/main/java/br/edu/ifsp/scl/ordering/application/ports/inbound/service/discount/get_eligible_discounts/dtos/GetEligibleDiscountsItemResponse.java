package br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

public record GetEligibleDiscountsItemResponse(DiscountId discountId, Double percentage) {

    public static GetEligibleDiscountsItemResponse createFromDiscount(Discount discount, Order order) {
        return new GetEligibleDiscountsItemResponse(
                discount.getDiscountId(),
                discount.getPercentage(order)
        );
    }
}
