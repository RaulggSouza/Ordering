package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountResponse;

public interface IApplyDiscountService {
    ApplyDiscountResponse apply(ApplyDiscountRequest request);
}
