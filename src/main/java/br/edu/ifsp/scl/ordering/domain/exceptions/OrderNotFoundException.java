package br.edu.ifsp.scl.ordering.domain.exceptions;

import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(OrderId orderId) {
        super("Order with id '" + orderId.value() + "' was not found");
    }
}
