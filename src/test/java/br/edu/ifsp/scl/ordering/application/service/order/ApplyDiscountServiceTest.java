package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.DiscountType;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.DuplicateDiscountTypeException;
import br.edu.ifsp.scl.ordering.domain.exceptions.IllegalOrderOperationException;
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
import static org.junit.jupiter.api.Assertions.fail;
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

        order = createOrderWithTotalAs100(orderId);
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
    @DisplayName("# 87 - Should throw IllegalOrderOperationException when applying two discounts of same kind")
    void shouldThrowIllegalOrderOperationExceptionWhenApplyingTwoDiscountsOfSameKind() {
        DiscountId secondDiscountId = new DiscountId("duplicated-discount-10-percent");
        Discount secondDiscount = createDiscountWith10Percent(secondDiscountId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));
        when(discountRepository.findById(secondDiscountId)).thenReturn(Optional.of(secondDiscount));

        ApplyDiscountRequest request = new ApplyDiscountRequest(orderId, List.of(discountId, secondDiscountId));

        assertThatExceptionOfType(DuplicateDiscountTypeException.class)
                .isThrownBy(() -> sut.apply(request));
    }

    private Order createOrderWithTotalAs100(OrderId orderId) {
        ProductId productId = new ProductId("product-value-100");
        OrderItem orderItem = new OrderItem(productId, 1, 100.0);
        return new Order(
                orderId,
                List.of(orderItem),
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
                LocalDateTime.now().minusHours(1)
        );
    }
}
