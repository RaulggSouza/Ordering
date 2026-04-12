package br.edu.ifsp.scl.ordering.infra.web.rest.discount.dtos.get_eligible_discounts;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsResponse;

import java.util.List;

public record GetEligibleDiscountsResponseDTO(
        List<GetEligibleDiscountsItemResponseDTO> items
) {
    public static GetEligibleDiscountsResponseDTO createFromServiceResponse(
            GetEligibleDiscountsResponse response
    ) {
        return new GetEligibleDiscountsResponseDTO(
                response.items().stream()
                        .map(GetEligibleDiscountsItemResponseDTO::createFromServiceResponse)
                        .toList()
        );
    }
}
