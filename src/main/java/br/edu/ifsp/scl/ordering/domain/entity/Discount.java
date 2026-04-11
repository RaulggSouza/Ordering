package br.edu.ifsp.scl.ordering.domain.entity;

import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

public class Discount {
    private final DiscountId discountId;

    public Discount(DiscountId discountId) {
        this.discountId = discountId;
    }

    public DiscountId getDiscountId() {
        return discountId;
    }
}
