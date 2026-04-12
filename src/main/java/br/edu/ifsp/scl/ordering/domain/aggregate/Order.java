package br.edu.ifsp.scl.ordering.domain.aggregate;

import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.List;
import java.util.UUID;

public class Order {
    private final OrderId id;
    private CustomerId customerId;
    private Address shippingAddress;
    private final List<OrderItem> items;
    private OrderStatus status;

    private Order(OrderId id, List<OrderItem> items, OrderStatus status) {
        this.id = id;
        this.items = items;
        this.status = status;
    }

    private Order(List<OrderItem> items, Address shippingAddress, CustomerId customerId) {
        this.id = new OrderId(UUID.randomUUID().toString());
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.customerId = customerId;
        this.status = OrderStatus.CREATED;
    }

    public static Order create(List<OrderItem> items, Address shippingAddress, CustomerId customerId){
        return new Order(items, shippingAddress, customerId);
    }

    public static Order createWithStatus(OrderId id, OrderStatus status){
        return new Order(id, List.of(), status);
    }

    public boolean ableToCancel(){
        if (status == OrderStatus.CREATED || status == OrderStatus.INVOICED) return true;
        return !(status == OrderStatus.SHIPPED) ;
    }

    public void cancelOrder() {
        this.status = OrderStatus.CANCELLED;
    }

    public OrderId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }


    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
