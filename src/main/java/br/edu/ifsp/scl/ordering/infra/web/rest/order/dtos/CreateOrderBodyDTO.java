package br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderRequest;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;

import java.util.List;

public record CreateOrderBodyDTO(String customerId,
                                 String street,
                                 String number,
                                 String city,
                                 String state,
                                 String postalCode,
                                 List<CreateOrderItemBodyDTO> items) {

    public CreateOrderRequest toRequest(){
        return new CreateOrderRequest(
                new CustomerId(customerId),
                new Address( street, number, city, state, postalCode ),
                items.stream().map(CreateOrderItemBodyDTO::toRequest).toList() );
    }
}
