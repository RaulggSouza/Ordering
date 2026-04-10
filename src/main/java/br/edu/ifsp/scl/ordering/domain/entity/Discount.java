package br.edu.ifsp.scl.ordering.domain.entity;

import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

public class Discount {
    private DiscountId id;
    private final double value;

    public Discount(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
