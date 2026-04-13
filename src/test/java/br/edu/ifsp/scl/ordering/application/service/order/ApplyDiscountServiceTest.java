package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.DiscountType;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.IllegalOrderOperationException;
import br.edu.ifsp.scl.ordering.domain.exceptions.MutipleDiscountTypeException;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;
import br.edu.ifsp.scl.ordering.domain.valueobject.MinimumValueDiscountRule;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import br.edu.ifsp.scl.ordering.testing.tags.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

    private OrderId orderId;
    private DiscountId discountId;
    private Order order;
    private Discount discount;

    @BeforeEach
    void setup() {
        orderId = new OrderId("order-1");
        discountId = new DiscountId("discount-1");

        order = createOrderWithTotalAs(orderId, 100.0);
        discount = createDiscountWith10Percent(discountId);
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#83 - Should add the selected discount to order discount list")
    void shouldAddTheSelectedDiscountToOrderDiscountList() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId));
        sut.apply(request);

        assertThat(order.getDiscounts()).contains(discount);

        verify(orderRepository).findById(orderId);
        verify(discountRepository).findById(discountId);
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#84 - Should update order total to gross total minus selected discounts")
    void shouldUpdateOrderTotalToGrossTotalMinusSelectedDiscounts() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId));

        sut.apply(request);
        assertThat(order.getTotal()).isEqualTo(90.0);

        verify(orderRepository).findById(orderId);
        verify(discountRepository).findById(discountId);
    }

    @TDD
    @UnitTest
    @ParameterizedTest(name = "Throwing for {0} order")
    @EnumSource(value = OrderStatus.class, names = {"CREATED"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("#85 - Should throw IllegalOrderOperationException for order with invalid status")
    void shouldThrowIllegalOrderOperationExceptionWhenApplyDiscountForCancelledOrder(OrderStatus status) {
        order = createOrderWithStatus(orderId, status);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.apply(request));

        verify(discountRepository, never()).findById(discountId);
    }

    @UnitTest
    @TDD
    @Test
    @DisplayName("#87 - Should throw MultipleDiscountTypeException when applying multiple discounts of same kind")
    void shouldThrowMultipleDiscountTypeExceptionWhenApplyingMultipleDiscountsOfSameKind() {
        DiscountId secondDiscountId = new DiscountId("duplicated-discount-10-percent");
        Discount secondDiscount = createDiscountWith10Percent(secondDiscountId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));
        when(discountRepository.findById(secondDiscountId)).thenReturn(Optional.of(secondDiscount));

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId, secondDiscountId));

        assertThatExceptionOfType(MutipleDiscountTypeException.class)
                .isThrownBy(() -> sut.apply(request));
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#88 - Should not allow the order net total to be less than zero when applying an eligible discount")
    void shouldNotAllowTheOrderNetTotalToBeLessThanZeroWhenApplyingAnEligibleDiscount() {
        DiscountId invalidDiscountId = new DiscountId("full-discount");
        Discount invalidDiscount = createDiscountWithPercentage(invalidDiscountId, 101);

        when(discountRepository.findById(invalidDiscountId)).thenReturn(Optional.of(invalidDiscount));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(invalidDiscountId));
        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.apply(request));
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

    private Discount createDiscountWith10Percent(DiscountId discountId) {
        return new Discount(
                discountId,
                new MinimumValueDiscountRule(0, 10),
                DiscountType.COUPON,
                true,
                LocalDateTime.now().plusHours(1)
        );
    }

    private Discount createDiscountWithPercentage(DiscountId fullDiscountId, double percentage) {
        return new Discount(
                fullDiscountId,
                new MinimumValueDiscountRule(0, percentage),
                DiscountType.COUPON,
                true,
                LocalDateTime.now().plusHours(1)
        );
    }
}
