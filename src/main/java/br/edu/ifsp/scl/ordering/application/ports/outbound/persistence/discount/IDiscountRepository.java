package br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount;

import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;

import java.util.Optional;

public interface IDiscountRepository {
    Optional<Discount> findById(DiscountId discountId);
}
