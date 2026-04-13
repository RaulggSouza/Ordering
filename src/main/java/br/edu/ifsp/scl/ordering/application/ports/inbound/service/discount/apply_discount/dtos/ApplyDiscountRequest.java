package br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.apply_discount.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;

public record ApplyDiscountRequest(OrderId orderId, List<DiscountId> discountIds) {
}
