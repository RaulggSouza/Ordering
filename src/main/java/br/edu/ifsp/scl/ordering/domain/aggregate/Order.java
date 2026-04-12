package br.edu.ifsp.scl.ordering.domain.aggregate;

import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;

public class Order {
    private final OrderId id;
    private CustomerId customerId;
    private Address shippingAddress;
    private final List<OrderItem> items;
    private OrderStatus status;

    public Order(OrderId id, CustomerId customerId, Address shippingAddress, List<OrderItem> items, OrderStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.items = items;
        this.status = status;
    }

    public boolean ableToCancel(){
        return status == OrderStatus.CREATED || status == OrderStatus.INVOICED;
    }

    public void cancelOrder() {
        this.status = OrderStatus.CANCELLED;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
