package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderItemResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.DiscountType;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderItemNotFoundException;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;
import br.edu.ifsp.scl.ordering.domain.valueobject.MinimumValueDiscountRule;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import br.edu.ifsp.scl.ordering.testing.tags.Functional;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import br.edu.ifsp.scl.ordering.testing.tags.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RemoveItemFromOrderServiceTest {
    @Mock
    IOrderRepository orderRepository;

    @Mock
    IProductRepository productRepository;

    @InjectMocks
    RemoveItemFromOrderService sut;

    @TDD
    @UnitTest
    @ParameterizedTest
    @DisplayName("#45 - Should remove item from order when order is created and has more than one item")
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1:1:100;2:2:50,1,2:2:50",
                    "1:1:100;2:2:50,2,1:1:100",
                    "1:1:100;2:2:50;3:1:30,2,1:1:100;3:1:30"
            }
    )
    void shouldRemoveItemFromOrderWhenOrderIsCreatedAndHasMoreThanOneItem(
            String itemsThatAlreadyExistsInOrderInput,
            String productIdToRemoveInput,
            String expectedOrderItemsInput
    ) {
        Order order = createOrder("1", itemsThatAlreadyExistsInOrderInput);
        ProductId productIdToRemove = new ProductId(productIdToRemoveInput);

        RemoveItemFromOrderRequest request = new RemoveItemFromOrderRequest(
                order.getOrderId(),
                productIdToRemove
        );

        List<RemoveItemFromOrderItemResponse> expectedOrderItems =
                createResponseRemoveItemFromOrder(expectedOrderItemsInput);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(productRepository.existsById(productIdToRemove)).thenReturn(true);

        RemoveItemFromOrderResponse response = sut.removeItemFromOrder(request);

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(productRepository, times(1)).existsById(productIdToRemove);
        verify(orderRepository, times(1)).save(order);

        assertThat(response.orderId()).isEqualTo(order.getOrderId());
        assertThat(response.items()).isEqualTo(expectedOrderItems);
    }

    @TDD
    @UnitTest
    @ParameterizedTest
    @DisplayName("#46 - Should remove item and remove only discounts that become ineligible")
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1:1:100;2:2:100,1,2:2:100,1:200:10;2:300:15,1:200:10",
                    "1:1:100;2:2:100,2,1:1:100,1:200:10;2:300:15,NULL",
                    "1:1:100;2:3:100,1,2:3:100,1:200:10;2:300:15,1:200:10;2:300:15"
            }
    )
    void shouldRemoveItemAndRemoveOnlyDiscountsThatBecomeIneligible(
            String itemsThatAlreadyExistsInOrderInput,
            String productIdToRemoveInput,
            String expectedOrderItemsInput,
            String appliedDiscountsInput,
            String expectedDiscountsInput
    ) {
        List<Discount> appliedDiscounts = createMinimumValueDiscounts(appliedDiscountsInput);

        Order order = createOrderWithDiscounts(
                "1",
                itemsThatAlreadyExistsInOrderInput,
                appliedDiscounts
        );

        ProductId productIdToRemove = new ProductId(productIdToRemoveInput);

        RemoveItemFromOrderRequest request = new RemoveItemFromOrderRequest(
                order.getOrderId(),
                productIdToRemove
        );

        List<RemoveItemFromOrderItemResponse> expectedOrderItems =
                createResponseRemoveItemFromOrder(expectedOrderItemsInput);

        List<DiscountId> expectedDiscountIds = createDiscountIds(expectedDiscountsInput);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(productRepository.existsById(productIdToRemove)).thenReturn(true);

        RemoveItemFromOrderResponse response = sut.removeItemFromOrder(request);

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(productRepository, times(1)).existsById(productIdToRemove);
        verify(orderRepository, times(1)).save(order);

        assertThat(response.orderId()).isEqualTo(order.getOrderId());
        assertThat(response.items()).isEqualTo(expectedOrderItems);

        assertThat(order.getDiscounts())
                .extracting(Discount::getDiscountId)
                .containsExactlyInAnyOrderElementsOf(expectedDiscountIds);
    }

    @Functional
    @UnitTest
    @ParameterizedTest
    @DisplayName("#48 - Should throw an error when item does not belong to order")
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1:1:100;2:2:50,3",
                    "1:1:100,2",
                    "2:2:50;3:1:30,1"
            }
    )
    void shouldThrowAnErrorWhenItemDoesNotBelongToOrder(
            String itemsThatAlreadyExistsInOrderInput,
            String productIdToRemoveInput
    ) {
        Order order = createOrder("1", itemsThatAlreadyExistsInOrderInput);
        ProductId productIdToRemove = new ProductId(productIdToRemoveInput);

        RemoveItemFromOrderRequest request = new RemoveItemFromOrderRequest(
                order.getOrderId(),
                productIdToRemove
        );

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(productRepository.existsById(productIdToRemove)).thenReturn(true);

        assertThatThrownBy(() -> sut.removeItemFromOrder(request))
                .isInstanceOf(OrderItemNotFoundException.class);

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(productRepository, times(1)).existsById(productIdToRemove);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @ParameterizedTest
    @DisplayName("#49 - Should throw an error when trying to remove the only item from order")
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1:1:100,1",
                    "2:3:50,2",
                    "7:2:30,7"
            }
    )
    void shouldThrowAnErrorWhenTryingToRemoveTheOnlyItemFromOrder(
            String itemsThatAlreadyExistsInOrderInput,
            String productIdToRemoveInput
    ) {
        Order order = createOrder("1", itemsThatAlreadyExistsInOrderInput);
        ProductId productIdToRemove = new ProductId(productIdToRemoveInput);

        RemoveItemFromOrderRequest request = new RemoveItemFromOrderRequest(
                order.getOrderId(),
                productIdToRemove
        );

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(productRepository.existsById(productIdToRemove)).thenReturn(true);

        assertThatThrownBy(() -> sut.removeItemFromOrder(request))
                .isInstanceOf(OrderMustHaveAtLeastOneItemException.class);

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(productRepository, times(1)).existsById(productIdToRemove);
        verify(orderRepository, never()).save(any());
    }

    private static Order createOrder(String orderId, String orderProductsInput) {
        return new Order(
                new OrderId(orderId),
                createOrderItems(orderProductsInput),
                List.of(),
                OrderStatus.CREATED,
                null,
                null
        );
    }

    private static Order createOrderWithDiscounts(
            String orderId,
            String orderProductsInput,
            List<Discount> discounts
    ) {
        return new Order(
                new OrderId(orderId),
                createOrderItems(orderProductsInput),
                discounts,
                OrderStatus.CREATED,
                null,
                null
        );
    }

    private static List<DiscountId> createDiscountIds(String discountsInput) {
        if (discountsInput == null || discountsInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(discountsInput.split(";"))
                .map(discountString -> {
                    String[] parts = discountString.split(":");
                    return new DiscountId(parts[0]);
                })
                .toList();
    }

    private static List<OrderItem> createOrderItems(String orderProductsInput) {
        if (orderProductsInput == null || orderProductsInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(orderProductsInput.split(";"))
                .map(productString -> {
                    String[] parts = productString.split(":");
                    String productId = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);

                    return new OrderItem(
                            new ProductId(productId),
                            quantity,
                            price
                    );
                })
                .toList();
    }



    private static List<Discount> createMinimumValueDiscounts(String discountsInput) {
        if (discountsInput == null || discountsInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(discountsInput.split(";"))
                .map(discountString -> {
                    String[] parts = discountString.split(":");
                    String discountId = parts[0];
                    double minimumValue = Double.parseDouble(parts[1]);
                    double percentage = Double.parseDouble(parts[2]);

                    return createMinimumValueDiscount(discountId, minimumValue, percentage);
                })
                .toList();
    }

    private static Discount createMinimumValueDiscount(
            String discountId,
            double minimumValue,
            double percentage
    ) {
        return new Discount(
                new DiscountId(discountId),
                new MinimumValueDiscountRule(minimumValue, percentage),
                DiscountType.COUPON,
                true,
                null
        );
    }

    private static List<RemoveItemFromOrderItemResponse> createResponseRemoveItemFromOrder(
            String orderProductsInput
    ) {
        if (orderProductsInput == null || orderProductsInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(orderProductsInput.split(";"))
                .map(productString -> {
                    String[] parts = productString.split(":");
                    String productId = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);

                    return new RemoveItemFromOrderItemResponse(
                            new ProductId(productId),
                            quantity,
                            price
                    );
                })
                .toList();
    }
}
