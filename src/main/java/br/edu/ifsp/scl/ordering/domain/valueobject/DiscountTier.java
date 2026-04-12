package br.edu.ifsp.scl.ordering.domain.valueobject;

public record DiscountTier(double minimumValue, double maximumValue) {
    public boolean contains(double value) {
        return value >= minimumValue && value <= maximumValue;
    }
}
