package br.edu.ifsp.scl.ordering.domain.valueobject;

public class DiscountTier {
    private final double minimumValue;
    private final double maximumValue;

    public DiscountTier(double minimumValue, double maximumValue) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public double getMinimumValue() {
        return minimumValue;
    }

    public boolean contains(double value) {
        return value >= minimumValue && value <= maximumValue;
    }
}
