package br.edu.ifsp.scl.ordering.domain.constant;

public enum OrderStatus {
    CREATED,
    INVOICED,
    SHIPPED,
    COMPLETED,
    CANCELLED;

    public boolean allowsGettingEligibleDiscounts() {
        return this == CREATED;
    }

    public boolean allowsAddItems() {
        return this == CREATED;
    }

    public boolean allowsUpdateItems() { return this == CREATED; }

    public boolean allowsRemoveItems() {return this == CREATED; }
}
