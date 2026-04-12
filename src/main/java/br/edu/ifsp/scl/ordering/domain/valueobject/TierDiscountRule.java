package br.edu.ifsp.scl.ordering.domain.valueobject;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.interfaces.DiscountRule;

import java.util.Comparator;
import java.util.List;

public record TierDiscountRule(List<DiscountTier> tiers) implements DiscountRule {

    public TierDiscountRule(List<DiscountTier> tiers) {
        this.tiers = tiers.stream()
                .sorted(Comparator.comparingDouble(DiscountTier::minimumValue))
                .toList();
    }

    @Override
    public boolean isEligible(Order order) {
        double total = order.getTotal();

        return tiers.stream().anyMatch(tier -> tier.contains(total));
    }

    @Override
    public double getPercentage(Order order) {
        double total = order.getTotal();

        return tiers.stream()
                .filter(tier -> tier.contains(total))
                .findFirst()
                .map(DiscountTier::percentage)
                .orElse(0.0);
    }
}