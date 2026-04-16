package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.apply_discount;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountRequest;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;

public record ApplyDiscountRequestDTO(List<String> discountIds) {
    public ApplyDiscountRequest toApplicationRequest(OrderId orderId) {
        return new ApplyDiscountRequest(
                orderId,
                discountIds.stream()
                        .map(DiscountId::new)
                        .toList()
        );
    }
}
