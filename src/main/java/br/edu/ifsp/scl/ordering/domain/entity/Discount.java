package br.edu.ifsp.scl.ordering.domain.entity;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.DiscountType;
import br.edu.ifsp.scl.ordering.domain.interfaces.DiscountRule;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

public class Discount {
    private final DiscountId discountId;
    private final DiscountType discountType;
    private final DiscountRule rule;
    private final boolean active;

    public Discount(DiscountId discountId, DiscountRule rule,  DiscountType discountTier,  boolean active) {
        this.discountId = discountId;
        this.rule = rule;
        this.discountType = discountTier;
        this.active = active;
    }

    public DiscountId getDiscountId() {
        return discountId;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isEligible(Order order) {
        return active
                && rule.isEligible(order)
                && doesNotHaveSameTypeApplied(order);
    }

    private boolean doesNotHaveSameTypeApplied(Order order) {
        return order.getDiscounts().stream()
                .noneMatch(discount -> discount.getDiscountType() == this.discountType);
    }
}
