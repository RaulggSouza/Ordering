package br.edu.ifsp.scl.ordering.infra.web.rest.discount;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.IGetEligibleDiscountsService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsResponse;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.infra.web.rest.discount.dtos.get_eligible_discounts.GetEligibleDiscountsRequestParamsDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.discount.dtos.get_eligible_discounts.GetEligibleDiscountsResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discounts")
public class DiscountController {
    private final IGetEligibleDiscountsService getEligibleDiscountsService;

    public DiscountController(IGetEligibleDiscountsService getEligibleDiscountsService) {
        this.getEligibleDiscountsService = getEligibleDiscountsService;
    }

    @GetMapping("/eligible")
    public ResponseEntity<GetEligibleDiscountsResponseDTO> getEligibleDiscounts(
            @ModelAttribute GetEligibleDiscountsRequestParamsDTO params
    ) {
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(
                new OrderId(params.orderId())
        );

        GetEligibleDiscountsResponse serviceResponse =
                getEligibleDiscountsService.getEligibleDiscounts(request);

        GetEligibleDiscountsResponseDTO response =
                GetEligibleDiscountsResponseDTO.createFromServiceResponse(serviceResponse);

        return ResponseEntity.ok(response);
    }
}
