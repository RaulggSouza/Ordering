package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.apply_discount;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountResponse;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;

import java.time.LocalDateTime;
import java.util.List;

public record ApplyDiscountResponseDTO(
        String orderId,
        List<AppliedDiscountDTO> appliedDiscounts
) {
    public static ApplyDiscountResponseDTO fromApplicationResponse(ApplyDiscountResponse response) {
        return new ApplyDiscountResponseDTO(
                response.orderId().value(),
                response.appliedDiscounts().stream()
                        .map(AppliedDiscountDTO::fromDomain)
                        .toList()
        );
    }

    public record AppliedDiscountDTO(
            String discountId,
            String discountType,
            boolean active,
            String expiration,
            LocalDateTime createdAt
    ) {
        public static AppliedDiscountDTO fromDomain(Discount discount) {
            return new AppliedDiscountDTO(
                    discount.getDiscountId().value(),
                    discount.getDiscountType().name(),
                    discount.isActive(),
                    discount.getExpiration(),
                    discount.getCreatedAt()
            );
        }
    }
}