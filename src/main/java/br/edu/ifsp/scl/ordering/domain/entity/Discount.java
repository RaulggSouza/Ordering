package br.edu.ifsp.scl.ordering.domain.entity;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.DiscountType;
import br.edu.ifsp.scl.ordering.domain.interfaces.DiscountRule;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Discount {
    private final DiscountId discountId;
    private final DiscountType discountType;
    private final DiscountRule rule;
    private final boolean active;
    private final LocalDateTime expiresAt;

    public Discount(DiscountId discountId, DiscountRule rule,  DiscountType discountTier,  boolean active, LocalDateTime expiresAt) {
        this.discountId = discountId;
        this.rule = rule;
        this.discountType = discountTier;
        this.active = active;
        this.expiresAt = expiresAt;
    }

    public DiscountId getDiscountId() {
        return discountId;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public String getExpiration() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
        return expiresAt.format(formatter);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isEligible(Order order) {
        return isActive()
                && !isExpired()
                && rule.isEligible(order)
                && doesNotHaveSameTypeApplied(order);
    }

    public boolean isStillEligible(Order order) {
        return active
                && !isExpired()
                && rule.isEligible(order);
    }

    private boolean doesNotHaveSameTypeApplied(Order order) {
        return order.getDiscounts().stream()
                .noneMatch(discount -> discount.getDiscountType() == this.discountType);
    }

    public double getPercentage(Order order) {
        return rule.getPercentage(order);
    }
}
