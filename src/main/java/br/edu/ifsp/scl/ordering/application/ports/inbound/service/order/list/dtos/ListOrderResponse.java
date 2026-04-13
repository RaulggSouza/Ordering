package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.list.dtos;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;

public record ListOrderResponse(OrderId orderId, OrderStatus status, List<ListOrderItemResponse> items, List<ListOrderDiscountResponse> discounts) {
    public static ListOrderResponse fromOrder(Order order){
        return new ListOrderResponse(
                order.getOrderId(),
                order.getOrderStatus(),
                order.getItems().stream().map(ListOrderItemResponse::fromOrderItem).toList(),
                order.getDiscounts().stream().map((discount) -> ListOrderDiscountResponse.fromDiscount(discount, order)).toList()
        );
    }
}
