package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos;

import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;

public record ApplyDiscountResponse(OrderId orderId, List<Discount> appliedDiscounts) {
}
