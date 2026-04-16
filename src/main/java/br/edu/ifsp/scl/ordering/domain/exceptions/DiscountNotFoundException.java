package br.edu.ifsp.scl.ordering.domain.exceptions;

import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

public class DiscountNotFoundException extends RuntimeException {
    public DiscountNotFoundException(DiscountId discountId) {
        super("Discount \"%s\" not found!".formatted(discountId.value()));
    }
}
