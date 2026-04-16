package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.exceptions.IllegalOrderOperationException;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import br.edu.ifsp.scl.ordering.testing.tags.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChangeOrderStatusServiceTest {
    @Mock
    private IOrderRepository orderRepository;
    @InjectMocks
    private ChangeOrderStatusService sut;

    @TDD
    @UnitTest
    @Test
    @DisplayName("#69 - Should update order status to INVOICED when payment is validated")
    void shouldUpdateOrderStatusToInvoicedWhenPaymentIsValidated() {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, OrderStatus.CREATED);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.INVOICED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ChangeOrderStatusResponse response = sut.change(request);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.INVOICED);
        assertThat(response.orderId()).isEqualTo(orderId);
        assertThat(response.previousStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.currentStatus()).isEqualTo(OrderStatus.INVOICED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#70 - Should throw IllegalOrderOperationException when trying to invoice cancelled order")
    void shouldThrowIllegalOrderOperationExceptionWhenTryingToInvoiceCancelledOrder() {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, OrderStatus.CANCELLED);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.INVOICED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.change(request));

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#71 - Should update order status to SHIPPED after invoiced order is delivered to carrier")
    void shouldUpdateOrderStatusToShippedAfterInvoicedOrderIsDeliveredToCarrier() {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, OrderStatus.INVOICED);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.SHIPPED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ChangeOrderStatusResponse response = sut.change(request);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(response.previousStatus()).isEqualTo(OrderStatus.INVOICED);
        assertThat(response.currentStatus()).isEqualTo(OrderStatus.SHIPPED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#73 - Should reject transition from CREATED to COMPLETED and require intermediate status")
    void shouldRejectTransitionFromCreatedToCompletedAndRequireIntermediateStatus() {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, OrderStatus.CREATED);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.COMPLETED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.change(request));

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#74 - Should update order status to COMPLETED when shipped order recieves delivery confirmation")
    void shouldUpdateOrderStatusToCompletedWhenShippedOrderRecievesDeliveryConfirmation() {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, OrderStatus.SHIPPED);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.COMPLETED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ChangeOrderStatusResponse response = sut.change(request);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(response.previousStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(response.currentStatus()).isEqualTo(OrderStatus.COMPLETED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @TDD
    @UnitTest
    @ParameterizedTest(name = "Target status: {0}")
    @EnumSource(value = OrderStatus.class, names = {"COMPLETED"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("#75 - Should block any further transition when order is already COMPLETED")
    void shouldBlockAnyFurtherTransitionWhenOrderIsAlreadyCompleted(OrderStatus targetStatus) {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, OrderStatus.COMPLETED);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, targetStatus);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.change(request));

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @ParameterizedTest(name = "Cancellable status: {0}")
    @EnumSource(value = OrderStatus.class, names = {"CREATED", "INVOICED"})
    @DisplayName("#77 - Should be able to cancel order in status CREATED or INVOICED")
    void shouldBeAbleToCancelOrderInStatusCreatedOrInvoiced(OrderStatus status) {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, status);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ChangeOrderStatusResponse response = sut.change(request);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(response.previousStatus()).isEqualTo(status);
        assertThat(response.currentStatus()).isEqualTo(OrderStatus.CANCELLED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @TDD
    @UnitTest
    @ParameterizedTest(name = "Non cancellable status: {0}")
    @EnumSource(value = OrderStatus.class, names = {"SHIPPED", "COMPLETED"})
    @DisplayName("#78 - Should reject cancellation from order after dispatch")
    void shouldRejectCancellationFromOrderAfterDispatch(OrderStatus status) {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, status);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.change(request));

        assertThat(order.getOrderStatus()).isEqualTo(status);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#79 - Should keep status as CANCELLED and reject repeated cancellation")
    void shouldKeepStatusAsCancelledAndRejectRepeatedCancellation() {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, OrderStatus.CANCELLED);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.change(request));

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @Test
    @DisplayName("#81 - Should keep status as INVOICED and reject transition back to CREATED")
    void shouldKeepStatusAsInvoicedAndRejectTransitionBackToCreated() {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, OrderStatus.INVOICED);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.CREATED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.change(request));

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.INVOICED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @ParameterizedTest(name = "Invalid target status from SHIPPED: {0}")
    @EnumSource(value = OrderStatus.class, names = {"INVOICED", "CREATED"})
    @DisplayName("#82 - Should keep status as SHIPPED and reject transition to INVOICED or CREATED")
    void shouldKeepStatusAsShippedAndRejectTransitionToInvoicedOrCreated(OrderStatus targetStatus) {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, OrderStatus.SHIPPED);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, targetStatus);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.change(request));

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.SHIPPED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
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
}
