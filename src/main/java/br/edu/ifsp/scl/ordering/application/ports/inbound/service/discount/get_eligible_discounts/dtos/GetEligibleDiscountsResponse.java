package br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos;

import br.edu.ifsp.scl.ordering.domain.entity.Discount;

import java.util.List;

public record GetEligibleDiscountsResponse(List<Discount> discounts) {
}
