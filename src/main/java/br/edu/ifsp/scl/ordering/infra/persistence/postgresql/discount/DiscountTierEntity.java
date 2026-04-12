package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.discount;

import jakarta.persistence.*;

@Entity
@Table(name = "discount_tiers")
public class DiscountTierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double minimumValue;

    @Column(nullable = false)
    private Double maximumValue;

    @Column(nullable = false)
    private Double percentage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discount_id", nullable = false)
    private DiscountEntity discount;

    protected DiscountTierEntity() {
    }

    public DiscountTierEntity(Double minimumValue, Double maximumValue, Double percentage) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.percentage = percentage;
    }

    public void setDiscount(DiscountEntity discount) {
        this.discount = discount;
    }

    public Long getId() {
        return id;
    }

    public Double getMinimumValue() {
        return minimumValue;
    }

    public Double getMaximumValue() {
        return maximumValue;
    }

    public Double getPercentage() {
        return percentage;
    }

    public DiscountEntity getDiscount() {
        return discount;
    }
}
