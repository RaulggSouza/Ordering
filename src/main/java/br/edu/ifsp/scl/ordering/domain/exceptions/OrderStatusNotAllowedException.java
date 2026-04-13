package br.edu.ifsp.scl.ordering.domain.exceptions;

import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;

public class OrderStatusNotAllowedException extends RuntimeException {
    public OrderStatusNotAllowedException(OrderStatus orderStatus) {
        super("Order status '" + orderStatus + "' does not allow the action");
    }
}
