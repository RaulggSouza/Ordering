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
    private OrderStatus status;
    private final List<Discount> discounts = new ArrayList<>();

    public Order(OrderId id) {
        this.id = id;
    }

    public List<Discount> getDiscounts() {
        return List.copyOf(discounts);
    }

    public void addDiscount(Discount discount) {
        discounts.add(discount);
    }

    public double getGrossTotal() {
        return 0;
    }

    public double getTotal() {
        return 90;
    }

    public void addItem(OrderItem orderItem) {

    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
