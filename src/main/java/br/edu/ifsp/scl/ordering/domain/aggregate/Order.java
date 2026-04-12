package br.edu.ifsp.scl.ordering.domain.aggregate;

import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.ProductsAlreadyExistInOrderException;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Order {
    private final OrderId id;
    private CustomerId customerId;
    private Address shippingAddress;
    private final List<OrderItem> items;
    private List<Discount> discounts;
    private OrderStatus status;

    public Order(
            OrderId id,
            List<OrderItem> items,
            List<Discount> discounts,
            OrderStatus status,
            CustomerId customerId,
            Address shippingAddress
    ) {
        this.id = id;
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.discounts = discounts == null ? new ArrayList<>() : new ArrayList<>(discounts);
        this.status = status;
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
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

    public static Order createWithStatus(OrderId id, OrderStatus status, CustomerId customerId, Address address){
        return new Order(id, List.of(), List.of(), status, customerId, address);
    }

    public boolean canBeCancelled(){
        return status == OrderStatus.CREATED || status == OrderStatus.INVOICED;
    }

    public void cancelOrder() {
        if (!canBeCancelled()) throw new IllegalStateException("Illegal status for cancellation. Status: "+ this.status);
        this.status = OrderStatus.CANCELLED;
    }

    public void addItems(List<OrderItem> itemsToAdd) {
        if (itemsToAdd == null || itemsToAdd.isEmpty()) {
            return;
        }

        Set<ProductId> existingProductIds = this.items.stream()
                .map(OrderItem::productId)
                .collect(Collectors.toSet());

        List<ProductId> duplicatedProductIds = itemsToAdd.stream()
                .map(OrderItem::productId)
                .filter(existingProductIds::contains)
                .distinct()
                .toList();

        if (!duplicatedProductIds.isEmpty()) {
            throw new ProductsAlreadyExistInOrderException(duplicatedProductIds);
        }

        this.items.addAll(itemsToAdd);
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

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }


    public List<OrderItem> getItems() {
        return items;
    }
}
