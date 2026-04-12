package br.edu.ifsp.scl.ordering.domain.valueobject;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.interfaces.DiscountRule;

public class MinimumValueDiscountRule implements DiscountRule {
    private final double minimumValue;
    private final double discountValue;

    public MinimumValueDiscountRule(double minimumValue, double discountValue) {
        this.minimumValue = minimumValue;
        this.discountValue = discountValue;
    }

    @Override
    public boolean isEligible(Order order) {
        return order.getTotal() >= minimumValue;
    }
}
