package br.edu.ifsp.scl.ordering.infra.web.rest.discount.dtos.get_eligible_discounts;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsItemResponse;

public record GetEligibleDiscountsItemResponseDTO(
        String discountId,
        Double percentage
) {
    public static GetEligibleDiscountsItemResponseDTO createFromServiceResponse(
            GetEligibleDiscountsItemResponse response
    ) {
        return new GetEligibleDiscountsItemResponseDTO(
                response.discountId().value(),
                response.percentage()
        );
    }
}