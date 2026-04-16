package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.exceptions.IllegalOrderOperationException;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderNotFoundException;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.testing.tags.Functional;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import br.edu.ifsp.scl.ordering.testing.tags.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChangeOrderStatusServiceTest {
    @Mock
    private IOrderRepository orderRepository;
    @InjectMocks
    private ChangeOrderStatusService sut;

    @TDD
    @UnitTest
    @ParameterizedTest(name = "Should allow transition from {0} to {1}")
    @CsvSource({
            "CREATED, INVOICED",
            "INVOICED, SHIPPED",
            "SHIPPED, COMPLETED"
    })
    @DisplayName("Should update order status for valid transitions")
    void shouldUpdateOrderStatusForValidTransitions(OrderStatus currentStatus, OrderStatus targetStatus) {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, currentStatus);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, targetStatus);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ChangeOrderStatusResponse response = sut.change(request);

        assertThat(order.getOrderStatus()).isEqualTo(targetStatus);
        assertThat(response.orderId()).isEqualTo(orderId);
        assertThat(response.previousStatus()).isEqualTo(currentStatus);
        assertThat(response.currentStatus()).isEqualTo(targetStatus);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @TDD
    @UnitTest
    @ParameterizedTest(name = "Should reject transition from {0} to {1}")
    @CsvSource({
            "CANCELLED, INVOICED",
            "CREATED, SHIPPED",
            "CREATED, COMPLETED",
            "INVOICED, CREATED",
            "INVOICED, COMPLETED",
            "SHIPPED, CREATED",
            "SHIPPED, INVOICED",
            "COMPLETED, CREATED",
            "COMPLETED, INVOICED",
            "COMPLETED, SHIPPED",
            "COMPLETED, CANCELLED"
    })
    @DisplayName("Should reject invalid non-cancellation transitions")
    void shouldRejectInvalidNonCancellationTransitions(OrderStatus currentStatus, OrderStatus targetStatus) {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, currentStatus);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, targetStatus);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.change(request));

        assertThat(order.getOrderStatus()).isEqualTo(currentStatus);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @ParameterizedTest(name = "Should cancel order from {0}")
    @EnumSource(value = OrderStatus.class, names = {"CREATED", "INVOICED"})
    @DisplayName("Should cancel order when current status is CREATED or INVOICED")
    void shouldCancelOrderWhenCurrentStatusIsCreatedOrInvoiced(OrderStatus currentStatus) {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, currentStatus);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ChangeOrderStatusResponse response = sut.change(request);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(response.orderId()).isEqualTo(orderId);
        assertThat(response.previousStatus()).isEqualTo(currentStatus);
        assertThat(response.currentStatus()).isEqualTo(OrderStatus.CANCELLED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @TDD
    @UnitTest
    @ParameterizedTest(name = "Should reject cancellation from {0}")
    @EnumSource(value = OrderStatus.class, names = {"SHIPPED", "COMPLETED", "CANCELLED"})
    @DisplayName("Should reject invalid cancellation transitions")
    void shouldRejectInvalidCancellationTransitions(OrderStatus currentStatus) {
        OrderId orderId = new OrderId("order-1");
        Order order = createOrderWithStatus(orderId, currentStatus);

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalOrderOperationException.class)
                .isThrownBy(() -> sut.change(request));

        assertThat(order.getOrderStatus()).isEqualTo(currentStatus);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @Functional
    @UnitTest
    @Test
    @DisplayName("#110 - Should throw OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
        OrderId orderId = new OrderId("order-1");

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(orderId, OrderStatus.INVOICED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> sut.change(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    private Order createOrderWithStatus(OrderId orderId, OrderStatus status) {
        return Order.createWithStatus(
                orderId,
                status,
                new CustomerId("customer-1"),
                mock(Address.class)
        );
    }
}
