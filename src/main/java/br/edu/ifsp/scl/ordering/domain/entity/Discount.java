package br.edu.ifsp.scl.ordering.domain.entity;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.interfaces.DiscountRule;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

public class Discount {
    private final DiscountId discountId;
    private final DiscountRule rule;

    public Discount(DiscountId discountId, DiscountRule rule) {
        this.discountId = discountId;
        this.rule = rule;
    }

    public DiscountId getDiscountId() {
        return discountId;
    }

    public boolean isEligible(Order order) {
        return rule.isEligible(order);
    }
}
