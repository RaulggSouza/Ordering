package br.edu.ifsp.scl.ordering.domain.interfaces;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;

public interface DiscountRule {
    boolean isEligible(Order order);
}
