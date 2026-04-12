package br.edu.ifsp.scl.ordering.application.service.discount;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.DiscountType;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderStatusNotAllowedException;
import br.edu.ifsp.scl.ordering.domain.valueobject.*;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetEligibleDiscountsServiceTest {
    @Mock
    IOrderRepository orderRepository;

    @Mock
    IDiscountRepository discountRepository;

    @InjectMocks
    GetEligibleDiscountsService sut;

    @TDD
    @DisplayName("#59 - should return all eligible discounts")
    @ParameterizedTest
    @CsvSource(
            value = {
                "1,1:1:100,1",
                "1,1:10:100-2:10:100,1:2",
                "1,1:100:100,1:2:3",
                "1,1:100:120,1:2"
            }
    )
    void shouldReturnAllEligibleDiscounts(String orderId, String orderProductsInput, String discountsIdsInput) {
        Order order = createOrder(orderId, orderProductsInput);
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());
        List<Discount> discounts = createDiscounts();

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(discountRepository.getAll()).thenReturn(discounts);

        List<DiscountId> discountIds = parseDiscountIds(discountsIdsInput);

        List<Discount> eligibleDiscounts = sut.getEligibleDiscounts(request).discounts();

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(discountRepository, times(1)).getAll();

        assertThat(eligibleDiscounts)
                .extracting(Discount::getDiscountId)
                .containsExactlyInAnyOrderElementsOf(discountIds);
    }

    @TDD
    @DisplayName("#60 - Should return an empty array if not eligible")
    @ParameterizedTest
    @CsvSource(
        value = {
            "1,1:1:1",
            "1,1:1:35",
        }
    )
    void shouldReturnEmptyArrayIfNotEligible(String orderId, String orderProductsInput) {
        Order order = createOrder(orderId, orderProductsInput);
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());
        List<Discount> discounts = createDiscounts();

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(discountRepository.getAll()).thenReturn(discounts);

        List<Discount> eligibleDiscounts = sut.getEligibleDiscounts(request).discounts();
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(discountRepository, times(1)).getAll();

        assertThat(eligibleDiscounts).isEmpty();
    }

    @TDD
    @DisplayName("#61 - Should not return any discount that has the same type as the discounts that are already applied on the order")
    @ParameterizedTest
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1,1:1:100,1,NULL",
                    "1,1:1:100,4,1",
                    "4,1:1:9500,3,1:2",
                    "5,1:1:45,4,NULL",
                    "10,1:1:10,1,NULL"
            }
    )
    void shouldNotReturnDiscountThatHasTheSameTypeAsTheDiscountsAppliedOnTheOrder(String orderId, String orderProductsInput, String orderDiscountsInput, String expectedDiscountsInput) {
        List<Discount> discounts = createDiscounts();

        List<DiscountId> orderDiscountIds = parseDiscountIds(orderDiscountsInput);

        List<Discount> orderDiscounts = discounts.stream().filter((discount) -> orderDiscountIds.contains(discount.getDiscountId())).toList();

        Order order = createOrderWithDiscounts(orderId, orderProductsInput, orderDiscounts);
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());

        List<DiscountId> expectedDiscountIds = parseDiscountIds(expectedDiscountsInput);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(discountRepository.getAll()).thenReturn(discounts);

        List<Discount> eligibleDiscounts = sut.getEligibleDiscounts(request).discounts();
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(discountRepository, times(1)).getAll();

        assertThat(eligibleDiscounts)
            .extracting(Discount::getDiscountId)
            .containsExactlyInAnyOrderElementsOf(expectedDiscountIds);

        assertThat(eligibleDiscounts)
                .extracting(Discount::getDiscountType)
                .doesNotContainAnyElementsOf(
                        orderDiscounts.stream()
                                .map(Discount::getDiscountType)
                                .toList()
                );
    }

    @TDD
    @DisplayName("#63 - should throw an error and not load discounts when order status is invalid")
    @ParameterizedTest
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "INVOICED",
                    "SHIPPED",
                    "COMPLETED",
                    "CANCELLED"
            }
    )
    void shouldThrowAnErrorAndNotLoadDiscountsWhenOrderStatusIsInvalid(String orderStatusInput) {
        OrderStatus orderStatus = OrderStatus.valueOf(orderStatusInput);
        Order order = Order.createWithStatus(new OrderId("1"), List.of(),  orderStatus);
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> sut.getEligibleDiscounts(request))
                .isInstanceOf(OrderStatusNotAllowedException.class);

        verify(orderRepository).findById(order.getOrderId());
        verify(discountRepository, never()).getAll();
    }

    @TDD
    @DisplayName("#64 - should return an empty array if no discounts registered")
    @Test
    void shouldReturnEmptyArrayIfNoDiscountsRegistered() {
        Order order = createOrder("1", "");
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(discountRepository.getAll()).thenReturn(new ArrayList<>());

        List<Discount> eligibleDiscounts = sut.getEligibleDiscounts(request).discounts();

        verify(orderRepository).findById(order.getOrderId());
        verify(discountRepository, times(1)).getAll();

        assertThat(eligibleDiscounts).isEmpty();

    }

    @TDD
    @DisplayName("#65 - Should consider the current order state when getting eligible discounts after items change")
    @ParameterizedTest
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1:1:100,1:1:10,1,NULL",
                    "1:1:100,1:1:2500,1,1:2",
                    "1:1:2500,1:1:10,1:2,NULL",
                    "1:1:45,1:1:100,4,1",
                    "1:1:9500,1:1:45,1:2:3,4"
            }
    )
    void shouldConsiderTheCurrentOrderStateWhenGettingEligibleDiscountsAfterItemsChange(
            String originalOrderProductsInput,
            String updatedOrderProductsInput,
            String firstExpectedDiscountIdsInput,
            String secondExpectedDiscountIdsInput
    ) {
        String orderId = "1";
        OrderId evaluatedOrderId = new OrderId(orderId);

        List<Discount> availableDiscounts = createDiscounts();
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(evaluatedOrderId);

        Order originalOrder = createOrder(orderId, originalOrderProductsInput);
        Order updatedOrder = createOrder(orderId, updatedOrderProductsInput);

        List<DiscountId> firstExpectedDiscountIds = parseDiscountIds(firstExpectedDiscountIdsInput);
        List<DiscountId> secondExpectedDiscountIds = parseDiscountIds(secondExpectedDiscountIdsInput);

        when(orderRepository.findById(evaluatedOrderId))
                .thenReturn(Optional.of(originalOrder))
                .thenReturn(Optional.of(updatedOrder));

        when(discountRepository.getAll()).thenReturn(availableDiscounts);

        List<Discount> firstEligibleDiscounts = sut.getEligibleDiscounts(request).discounts();
        List<Discount> secondEligibleDiscounts = sut.getEligibleDiscounts(request).discounts();

        verify(orderRepository, times(2)).findById(evaluatedOrderId);
        verify(discountRepository, times(2)).getAll();

        assertThat(firstEligibleDiscounts)
                .extracting(Discount::getDiscountId)
                .containsExactlyInAnyOrderElementsOf(firstExpectedDiscountIds);

        assertThat(secondEligibleDiscounts)
                .extracting(Discount::getDiscountId)
                .containsExactlyInAnyOrderElementsOf(secondExpectedDiscountIds);
    }

    @TDD
    @DisplayName("#66 - Should return only active discounts")
    @ParameterizedTest
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1,1:1:100,1",
                    "1,1:10:100,1",
                    "1,1:100:100,1:3",
                    "1,1:1:45,NULL"
            }
    )
    void shouldReturnOnlyActiveDiscounts(
            String orderId,
            String orderProductsInput,
            String discountsIdsInput
    ) {
        Order order = createOrder(orderId, orderProductsInput);
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());
        List<Discount> discounts = createDiscountsWithActiveAndInactive();

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(discountRepository.getAll()).thenReturn(discounts);

        List<DiscountId> expectedDiscountIds = parseDiscountIds(discountsIdsInput);

        List<Discount> eligibleDiscounts = sut.getEligibleDiscounts(request).discounts();

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(discountRepository, times(1)).getAll();

        assertThat(eligibleDiscounts)
                .extracting(Discount::getDiscountId)
                .containsExactlyInAnyOrderElementsOf(expectedDiscountIds);

        assertThat(eligibleDiscounts)
                .allMatch(Discount::isActive);
    }

    @TDD
    @DisplayName("#67 - Should return only non expired discounts")
    @ParameterizedTest
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1,1:1:100,1",
                    "1,1:100:100,1:3",
                    "1,1:1:45,NULL",
                    "1,1:1:9500,1:3"
            }
    )
    void shouldReturnOnlyNonExpiredDiscounts(
            String orderId,
            String orderProductsInput,
            String expectedDiscountIdsInput
    ) {
        Order order = createOrder(orderId, orderProductsInput);
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());

        List<Discount> discounts = createDiscountsWithExpiredAndValidDates();
        List<DiscountId> expectedDiscountIds = parseDiscountIds(expectedDiscountIdsInput);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(discountRepository.getAll()).thenReturn(discounts);

        List<Discount> eligibleDiscounts = sut.getEligibleDiscounts(request).discounts();

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(discountRepository, times(1)).getAll();

        assertThat(eligibleDiscounts)
                .extracting(Discount::getDiscountId)
                .containsExactlyInAnyOrderElementsOf(expectedDiscountIds);
    }



    private static List<Discount> createDiscounts(){
        return List.of(
                new Discount(new DiscountId("1"), new MinimumValueDiscountRule(100, 1), DiscountType.CATEGORY, true, null),
                new Discount(new DiscountId("2"), new MinimumValueDiscountRule(2000, 1), DiscountType.COUPON, true, null),
                new Discount(new DiscountId("3"), new TierDiscountRule(List.of(new DiscountTier(9000, 11000))), DiscountType.FIRST_PURCHASE, true, null),
                new Discount(new DiscountId("4"), new TierDiscountRule(List.of(new DiscountTier(20, 30), new DiscountTier(40, 50))), DiscountType.SEASONAL, true, null)
        );
    }

    private static List<Discount> createDiscountsWithActiveAndInactive() {
        return List.of(
                new Discount(
                        new DiscountId("1"),
                        new MinimumValueDiscountRule(100, 1),
                        DiscountType.CATEGORY,
                        true,
                        null
                ),
                new Discount(
                        new DiscountId("2"),
                        new MinimumValueDiscountRule(2000, 1),
                        DiscountType.COUPON,
                        false,
                        null
                ),
                new Discount(
                        new DiscountId("3"),
                        new TierDiscountRule(List.of(new DiscountTier(9000, 11000))),
                        DiscountType.FIRST_PURCHASE,
                        true,
                        null
                ),
                new Discount(
                        new DiscountId("4"),
                        new TierDiscountRule(List.of(new DiscountTier(20, 30), new DiscountTier(40, 50))),
                        DiscountType.SEASONAL,
                        false,
                        null
                )
        );
    }

    private static List<Discount> createDiscountsWithExpiredAndValidDates() {
        return List.of(
                new Discount(
                        new DiscountId("1"),
                        new MinimumValueDiscountRule(100, 1),
                        DiscountType.CATEGORY,
                        true,
                        LocalDateTime.of(2026, 4, 20, 23, 59)
                ),
                new Discount(
                        new DiscountId("2"),
                        new MinimumValueDiscountRule(2000, 1),
                        DiscountType.COUPON,
                        true,
                        LocalDateTime.of(2026, 4, 1, 23, 59)
                ),
                new Discount(
                        new DiscountId("3"),
                        new TierDiscountRule(List.of(new DiscountTier(9000, 11000))),
                        DiscountType.FIRST_PURCHASE,
                        true,
                        LocalDateTime.of(2026, 4, 30, 23, 59)
                ),
                new Discount(
                        new DiscountId("4"),
                        new TierDiscountRule(List.of(new DiscountTier(20, 30), new DiscountTier(40, 50))),
                        DiscountType.SEASONAL,
                        true,
                        LocalDateTime.of(2026, 4, 5, 23, 59)
                )
        );
    }

    private static List<DiscountId> parseDiscountIds(String discountIdsInput) {
        if (discountIdsInput == null || discountIdsInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(discountIdsInput.split(":"))
                .filter(discountId -> !discountId.isBlank())
                .map(DiscountId::new)
                .toList();
    }

    private static Order createOrder(String orderId, String orderProductsInput) {
        return Order.create(
                new OrderId(orderId),
                createOrderItems(orderProductsInput)
        );
    }

    private static Order createOrderWithDiscounts(
            String orderId,
            String orderProductsInput,
            List<Discount> discounts
    ) {
        return Order.createWithDiscounts(
                new OrderId(orderId),
                createOrderItems(orderProductsInput),
                discounts
        );
    }

    private static Order createOrderWithStatus(
            String orderId,
            String orderProductsInput,
            OrderStatus status
    ) {
        return Order.createWithStatus(
                new OrderId(orderId),
                createOrderItems(orderProductsInput),
                status
        );
    }

    private static List<OrderItem> createOrderItems(String orderProductsInput) {
        if (orderProductsInput == null || orderProductsInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(orderProductsInput.split("-"))
                .map(productString -> {
                    String[] parts = productString.split(":");
                    String productId = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);

                    return new OrderItem(new ProductId(productId), quantity, price);
                })
                .toList();
    }
}
