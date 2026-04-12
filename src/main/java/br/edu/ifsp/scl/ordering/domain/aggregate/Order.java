package br.edu.ifsp.scl.ordering.domain.aggregate;

import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final OrderId id;
    private CustomerId customerId;
    private Address shippingAddress;
    private List<OrderItem> items;
    private List<Discount> discounts;
    private OrderStatus status;

    public Order(
            OrderId id,
            List<OrderItem> items,
            List<Discount> discounts,
            OrderStatus status
    ) {
        this.id = id;
        this.items = new ArrayList<>(items);
        this.discounts = new ArrayList<>(discounts);
        this.status = status;
    }

    public static Order create(OrderId id, List<OrderItem> items) {
        return new Order(id, items, List.of(), OrderStatus.CREATED);
    }

    public static Order createWithDiscounts(OrderId id, List<OrderItem> items, List<Discount> discounts) {
        return new Order(id, items, discounts, OrderStatus.CREATED);
    }

    public static Order createWithStatus(OrderId id, List<OrderItem> items, OrderStatus status) {
        return new Order(id, items, List.of(), status);
    }

    public OrderId getOrderId() {
        return id;
    }

    public OrderStatus getOrderStatus() {
        return status;
    }

    public List<Discount> getDiscounts() {
        return List.copyOf(discounts);
    }

    public double getTotal(){
        return items.stream().mapToDouble(OrderItem::getTotal).sum();
    }
}
