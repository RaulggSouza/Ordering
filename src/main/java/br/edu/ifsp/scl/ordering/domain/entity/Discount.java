package br.edu.ifsp.scl.ordering.domain.entity;

import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

public class Discount {
    private final DiscountId discountId;
    private final double minOrderValue;

    public Discount(DiscountId discountId, double minOrderValue) {
        this.discountId = discountId;
        this.minOrderValue = minOrderValue;
    }

    public DiscountId getDiscountId() {
        return discountId;
    }

    public double getMinOrderValue() {
        return minOrderValue;
    }
}
