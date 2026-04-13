package br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsResponse;

public interface IGetEligibleDiscountsService {
    GetEligibleDiscountsResponse getEligibleDiscounts(GetEligibleDiscountsRequest request);
}
