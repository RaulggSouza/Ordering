package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.discount;

import br.edu.ifsp.scl.ordering.domain.constant.DiscountType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "discounts")
public class DiscountEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private boolean active;

    @Column
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountRuleType ruleType;

    @Column
    private Double minimumValue;

    @Column
    private Double discountValue;

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscountTierEntity> tiers = new ArrayList<>();

    protected DiscountEntity() {
    }

    public DiscountEntity(
            String id,
            DiscountType discountType,
            boolean active,
            LocalDateTime expiresAt,
            DiscountRuleType ruleType,
            Double minimumValue,
            Double discountValue
    ) {
        this.id = id;
        this.discountType = discountType;
        this.active = active;
        this.expiresAt = expiresAt;
        this.ruleType = ruleType;
        this.minimumValue = minimumValue;
        this.discountValue = discountValue;
    }

    public void addTier(DiscountTierEntity tier) {
        tier.setDiscount(this);
        this.tiers.add(tier);
    }

    public String getId() {
        return id;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public DiscountRuleType getRuleType() {
        return ruleType;
    }

    public Double getMinimumValue() {
        return minimumValue;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public List<DiscountTierEntity> getTiers() {
        return tiers;
    }
}
