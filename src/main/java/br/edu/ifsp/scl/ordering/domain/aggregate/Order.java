package br.edu.ifsp.scl.ordering.domain.aggregate;

import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.*;
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
    private double evaluatedTotal;

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

    public static Order create(List<OrderItem> items, Address shippingAddress, CustomerId customerId) {
        return new Order(items, shippingAddress, customerId);
    }

    public static Order createWithStatus(OrderId id, OrderStatus status, CustomerId customerId, Address address) {
        return new Order(id, List.of(), List.of(), status, customerId, address);
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.CREATED || status == OrderStatus.INVOICED;
    }

    public void cancelOrder() {
        if (!canBeCancelled())
            throw new IllegalStateException("Illegal status for cancellation. Status: " + this.status);
        this.status = OrderStatus.CANCELLED;
    }

    public void removeItem(ProductId productId) {
        if (!this.status.allowsRemoveItems()) {
            throw new OrderStatusNotAllowedException(this.getOrderStatus());
        }

        boolean itemExistsInOrder = this.items.stream()
                .anyMatch(item -> item.productId().equals(productId));

        if (!itemExistsInOrder) {
            throw new OrderItemNotFoundException(productId);
        }

        if (this.items.size() == 1) {
            throw new OrderMustHaveAtLeastOneItemException();
        }

        this.items.removeIf(item -> item.productId().equals(productId));

        removeIneligibleDiscounts();
        recalculateTotal();
    }

    public void addItems(List<OrderItem> itemsToAdd) {
        if (itemsToAdd == null || itemsToAdd.isEmpty()) {
            return;
        }

        if (!this.status.allowsAddItems()) {
            throw new OrderStatusNotAllowedException(this.getOrderStatus());
        }

        List<ProductId> invalidQuantityProductIds = itemsToAdd.stream()
                .filter(item -> item.quantity() <= 0)
                .map(OrderItem::productId)
                .distinct()
                .toList();

        if (!invalidQuantityProductIds.isEmpty()) {
            throw new InvalidOrderItemQuantityException(invalidQuantityProductIds);
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
        recalculateTotal();
    }

    public void updateItemQuantity(ProductId productId, int quantity) {
        if (!this.status.allowsUpdateItems()) {
            throw new OrderStatusNotAllowedException(this.getOrderStatus());
        }

        if (quantity <= 0) {
            throw new InvalidOrderItemQuantityException(List.of(productId));
        }

        boolean updated = false;

        for (int index = 0; index < this.items.size(); index++) {
            OrderItem currentItem = this.items.get(index);

            if (currentItem.productId().equals(productId)) {
                OrderItem updatedItem = new OrderItem(
                        currentItem.productId(),
                        quantity,
                        currentItem.price()
                );

                this.items.set(index, updatedItem);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new OrderItemNotFoundException(productId);
        }

        removeIneligibleDiscounts();
        recalculateTotal();
    }

    private void removeIneligibleDiscounts() {
        this.discounts.removeIf(discount -> !discount.isStillEligible(this));
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

    public double getGrossTotal() {
        return items.stream()
                .mapToDouble(OrderItem::getTotal)
                .sum();
    }

    public double getTotal() {
        return evaluatedTotal;
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

    public void addDiscount(Discount discount) {
        this.discounts.add(discount);
        recalculateTotal();
    }

    private void recalculateTotal() {
        double evaluation = getGrossTotal();
        for (Discount discount : discounts) {
            double deduction = discount.getPercentage(this) / 100;
            double discountValue = evaluation * deduction;
            evaluation -= discountValue;
        }
        this.evaluatedTotal = evaluation;
    }

    public void changeStatusTo(OrderStatus orderStatus) {
        if (this.status == OrderStatus.CANCELLED)
            throw new IllegalOrderOperationException(
                    "Cannot change status from CANCELLED to \"%s\".".formatted(orderStatus)
            );

        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalOrderOperationException(
                    "Cannot change status from COMPLETED to \"%s\".".formatted(orderStatus)
            );
        }

        boolean isInvalidTransition = this.status == OrderStatus.CREATED
                && orderStatus == OrderStatus.COMPLETED;

        if (isInvalidTransition)
            throw new IllegalOrderOperationException(
                    "Illegal status transition from \"%s\" to \"%s\"."
                            .formatted(this.status, orderStatus)
            );

        this.status = orderStatus;
    }
}
