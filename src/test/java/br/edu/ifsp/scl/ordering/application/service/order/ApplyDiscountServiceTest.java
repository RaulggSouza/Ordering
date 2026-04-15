package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.DiscountType;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.*;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;
import br.edu.ifsp.scl.ordering.domain.valueobject.MinimumValueDiscountRule;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import br.edu.ifsp.scl.ordering.testing.tags.Functional;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import br.edu.ifsp.scl.ordering.testing.tags.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplyDiscountServiceTest {
    @Mock
    private IDiscountRepository discountRepository;
    @Mock
    private IOrderRepository orderRepository;
    @InjectMocks
    private ApplyDiscountService sut;

    @TDD
    @UnitTest
    @Test
    @DisplayName("#83 - Should add the selected discount to order discount list")
    void shouldAddTheSelectedDiscountToOrderDiscountList() {
        OrderId orderId = new OrderId("order-1");
        DiscountId discountId = new DiscountId("discount-10");

        Order order = createOrderWithTotalAs(orderId, 100.0);
        Discount discount = createDiscount(discountId, DiscountType.COUPON, 10.0);

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));

        ApplyDiscountResponse response = sut.apply(request);

        assertThat(order.getDiscounts()).contains(discount);
        assertThat(response.appliedDiscounts()).contains(discount);
        assertThat(order.getTotal()).isEqualTo(90.0);

        verify(orderRepository, times(1)).findById(orderId);
        verify(discountRepository, times(1)).findById(discountId);
        verify(orderRepository, times(1)).save(order);
    }

    @TDD
    @UnitTest
    @ParameterizedTest(name = "Invalid status: {0}")
    @EnumSource(value = OrderStatus.class, names = {"CREATED"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("#85 - Should throw IllegalOrderOperationException for order with invalid status")
    void shouldThrowIllegalOrderOperationExceptionWhenApplyDiscountForCancelledOrder(OrderStatus status) {
        OrderId orderId = new OrderId("order-id");
        DiscountId discountId = new DiscountId("discount-1");

        Order order = createOrderWithStatus(orderId, status);
        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.apply(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(discountRepository, never()).findById(discountId);
        verify(orderRepository, never()).save(order);
    }

    @UnitTest
    @TDD
    @Test
    @DisplayName("#87 - Should throw MultipleDiscountTypeException when applying multiple discounts of same kind")
    void shouldThrowMultipleDiscountTypeExceptionWhenApplyingMultipleDiscountsOfSameKind() {
        OrderId orderId = new OrderId("order-1");
        DiscountId firstDiscountId = new DiscountId("discount-1");
        DiscountId secondDiscountId = new DiscountId("discount-2");

        Order order = createOrderWithTotalAs(orderId, 100.0);
        Discount firstDiscount = createDiscount(firstDiscountId, DiscountType.COUPON, 10);
        Discount secondDiscount = createDiscount(secondDiscountId, DiscountType.COUPON, 15);

        ApplyDiscountRequest request = new ApplyDiscountRequest(
                orderId,
                List.of(firstDiscountId, secondDiscountId)
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(firstDiscountId)).thenReturn(Optional.of(firstDiscount));
        when(discountRepository.findById(secondDiscountId)).thenReturn(Optional.of(secondDiscount));

        assertThatExceptionOfType(MutipleDiscountTypeException.class)
                .isThrownBy(() -> sut.apply(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(discountRepository, times(1)).findById(firstDiscountId);
        verify(discountRepository, times(1)).findById(secondDiscountId);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @ParameterizedTest
    @ValueSource(doubles = { 100.1, 101.0, 150.0, 200.0 })
    @DisplayName("#88 - Should not allow the order net total to be less than zero when applying an eligible discount")
    void shouldNotAllowTheOrderNetTotalToBeLessThanZeroWhenApplyingAnEligibleDiscount(double percentage) {
        OrderId orderId = new OrderId("order-1");
        DiscountId discountId = new DiscountId("full-discount");

        Order order = createOrderWithTotalAs(orderId, 100.0);
        Discount discount = createDiscount(discountId, DiscountType.COUPON, percentage);

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.apply(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(discountRepository, times(1)).findById(discountId);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#89 - Should accumulate multiple eligible discount applications")
    void shouldAccumulateMultipleEligibleDiscountApplications() {
        OrderId orderId = new OrderId("order-1");
        DiscountId firstDiscountId = new DiscountId("discount-10");
        DiscountId secondDiscountId = new DiscountId("discount-5");

        Order order = createOrderWithTotalAs(orderId, 100.0);

        Discount firstDiscount = createDiscount(
                firstDiscountId,
                DiscountType.COUPON,
                10
        );

        Discount secondDiscount = createDiscount(
                secondDiscountId,
                DiscountType.SEASONAL,
                5
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        when(discountRepository.findById(firstDiscountId))
                .thenReturn(Optional.of(firstDiscount));

        when(discountRepository.findById(secondDiscountId))
                .thenReturn(Optional.of(secondDiscount));

        sut.apply(new ApplyDiscountRequest(orderId, List.of(firstDiscountId)));
        sut.apply(new ApplyDiscountRequest(orderId, List.of(secondDiscountId)));

        assertThat(order.getDiscounts())
                .containsExactly(firstDiscount, secondDiscount);

        assertThat(order.getTotal()).isEqualTo(85.5);

        verify(orderRepository, times(2)).findById(orderId);
        verify(discountRepository, times(1)).findById(firstDiscountId);
        verify(discountRepository, times(1)).findById(secondDiscountId);
        verify(orderRepository, times(2)).save(order);
    }

    @UnitTest
    @TDD
    @Test
    @DisplayName("#90 - Should throw ExpiredDiscountException when discount expires before application confirmation")
    void shouldThrowExpiredDiscountExceptionWhenDiscountExpiresBeforeApplicationConfirmation() {
        OrderId orderId = new OrderId("order-1");
        DiscountId discountId = new DiscountId("expired-discount");

        Order order = createOrderWithTotalAs(orderId, 100.0);
        Discount expiredDiscount = createExpiredDiscount(discountId);

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(expiredDiscount));

        assertThatExceptionOfType(ExpiredDiscountException.class)
                .isThrownBy(() -> sut.apply(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(discountRepository, times(1)).findById(discountId);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#91 - Should throw InvalidDiscountException when discount start date is after current system date")
    void shouldThrowInvalidDiscountExceptionWhenDiscountStartDateIsAfterCurrentSystemDate() {
        OrderId orderId = new OrderId("order-1");
        DiscountId discountId = new DiscountId("future-discount");

        Order order = createOrderWithTotalAs(orderId, 100.0);
        Discount futureDiscount = createDiscountStartingInFuture(discountId);

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(futureDiscount));

        assertThatExceptionOfType(InvalidDiscountException.class)
                .isThrownBy(() -> sut.apply(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(discountRepository, times(1)).findById(discountId);
        verify(orderRepository, never()).save(any());
    }

    @Functional
    @UnitTest
    @Test
    @DisplayName("#110 - Should throw OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
        OrderId orderId = new OrderId("order-1");
        DiscountId discountId = new DiscountId("discount-1");

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> sut.apply(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(discountRepository, never()).findById(discountId);
        verify(orderRepository, never()).save(any());
    }

    @Functional
    @UnitTest
    @Test
    @DisplayName("#111 - Should throw DiscountNotFoundException for a non-existing discount in request")
    void shouldThrowDiscountNotFoundExceptionForANonExistingDiscountInRequest() {
        OrderId orderId = new OrderId("order-1");
        DiscountId discountId = new DiscountId("discount-1");

        Order order = createOrderWithTotalAs(orderId, 100.0);
        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(discountId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(DiscountNotFoundException.class)
                .isThrownBy(() -> sut.apply(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(discountRepository, times(1)).findById(discountId);
        verify(orderRepository, never()).save(any());
    }

    @Functional
    @UnitTest
    @Test
    @DisplayName("#112 - Should throw InvalidDiscountException for application without any discounts in request")
    void shouldThrowInvalidDiscountExceptionForApplicationWithoutAnyDiscountsInRequest() {
        OrderId orderId = new OrderId("order-1");
        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of());

        assertThatExceptionOfType(InvalidDiscountException.class)
                .isThrownBy(() -> sut.apply(request));

        verify(orderRepository, never()).findById(orderId);
        verify(discountRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }

    private Order createOrderWithTotalAs(OrderId orderId, double total) {
        OrderItem item = new OrderItem(new ProductId("sample"), 1, total);
        return new Order(
                orderId,
                List.of(item),
                List.of(),
                OrderStatus.CREATED,
                null,
                null
        );
    }

    private Order createOrderWithStatus(OrderId orderId, OrderStatus status) {
        return new Order(
                orderId,
                List.of(),
                List.of(),
                status,
                null,
                null
        );
    }

    private Discount createDiscount(DiscountId discountId, DiscountType type, double percentage) {
        return new Discount(
                discountId,
                new MinimumValueDiscountRule(0.0, percentage),
                type,
                true,
                LocalDateTime.now().plusHours(1)
        );
    }

    private Discount createExpiredDiscount(DiscountId discountId) {
        return new Discount(
                discountId,
                new MinimumValueDiscountRule(0, 10),
                DiscountType.COUPON,
                true,
                LocalDateTime.now().minusMinutes(1)
        );
    }

    private Discount createDiscountStartingInFuture(DiscountId discountId) {
        return new Discount(
                discountId,
                new MinimumValueDiscountRule(0, 10),
                DiscountType.COUPON,
                true,
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusHours(1)
        );
    }
}
