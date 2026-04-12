package br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos;

import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

public record GetEligibleDiscountsRequest(OrderId orderId) {
}
