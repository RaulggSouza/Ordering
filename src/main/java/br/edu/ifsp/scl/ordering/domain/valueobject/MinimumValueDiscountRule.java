package br.edu.ifsp.scl.ordering.domain.valueobject;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.interfaces.DiscountRule;

public record MinimumValueDiscountRule(double minimumValue, double discountValue) implements DiscountRule {

    @Override
    public boolean isEligible(Order order) {
        return order.getTotal() >= minimumValue;
    }

    @Override
    public double getPercentage(Order order) {
        return discountValue;
    }
}
