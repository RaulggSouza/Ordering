package br.edu.ifsp.scl.ordering.domain.valueobject;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.interfaces.DiscountRule;

import java.util.Comparator;
import java.util.List;

public class TierDiscountRule implements DiscountRule {
    private final List<DiscountTier> tiers;

    public TierDiscountRule(List<DiscountTier> tiers) {
        this.tiers = tiers.stream()
                .sorted(Comparator.comparingDouble(DiscountTier::getMinimumValue))
                .toList();
    }

    @Override
    public boolean isEligible(Order order) {
        return tiers.stream()
                .anyMatch(tier -> order.getTotal() >= tier.getMinimumValue());
    }
}