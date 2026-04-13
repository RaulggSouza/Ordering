package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.list.dtos;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.DiscountType;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

public record ListOrderDiscountResponse(DiscountId discountId, double percentage, DiscountType discountType) {
    public static ListOrderDiscountResponse fromDiscount(Discount discount, Order order){
        return new ListOrderDiscountResponse(discount.getDiscountId(), discount.getPercentage(order), discount.getDiscountType());
    }
}
