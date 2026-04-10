package br.edu.ifsp.scl.ordering.domain.entity;

import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

public class Discount {
    private final DiscountId id;
    private final double value;

    public Discount(DiscountId id, double value) {
        this.id = id;
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
