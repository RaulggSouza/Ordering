package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.dtos.CancelOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.exceptions.OrderNotFoundException;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.testing.tags.Functional;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CancelOrderServiceTest {
    @Mock
    IOrderRepository orderRepository;

    @InjectMocks
    CancelOrderService sut;

    @UnitTest
    @TDD
    @ParameterizedTest(name = "[{index}]: Should cancel if order status is {0}")
    @EnumSource(value = OrderStatus.class, names = {"CREATED", "INVOICED"})
    @DisplayName("Should cancel an order if its status is valid")
    void shouldCancelAnOrderIfItsStatusIsValid(OrderStatus status) {
        OrderId orderId = new OrderId("123");
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        Order order = createOrderWithStatus(status);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        sut.cancel(request);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @UnitTest
    @Functional
    @Test
    @DisplayName("Should throw IllegalStateException if order status is SHIPPED")
    void shouldThrowIllegalStateExceptionIfOrderStatusIsShipped() {
        OrderId orderId = new OrderId("123");
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        Order order = createOrderWithStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatIllegalStateException().isThrownBy(() -> sut.cancel(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(order);
    }

    @UnitTest
    @Functional
    @Test
    @DisplayName("Should throw IllegalStateException if order status is COMPLETED")
    void shouldThrowIllegalStateExceptionIfOrderStatusIsCompleted() {
        OrderId orderId = new OrderId("123");
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        Order order = createOrderWithStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatIllegalStateException().isThrownBy(() -> sut.cancel(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(order);
    }

    @UnitTest
    @Functional
    @Test
    @DisplayName("Should throw IllegalStateException if order status is CANCELLED")
    void shouldThrowIllegalStateExceptionIfOrderStatusIsCancelled() {
        OrderId orderId = new OrderId("123");
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        Order order = createOrderWithStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatIllegalStateException().isThrownBy(() -> sut.cancel(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(order);
    }

    @UnitTest
    @Functional
    @Test
    @DisplayName("Should throw OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
        OrderId orderId = new OrderId("123");
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        Order order = createOrderWithStatus(OrderStatus.CREATED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(OrderNotFoundException.class).isThrownBy(() -> sut.cancel(request));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(order);
    }

    @UnitTest
    @Functional
    @Test
    @DisplayName("Should throw NullPointerException when id is null")
    void shouldThrowNullPointerExceptionWhenIdIsNull() {
        CancelOrderRequest request = new CancelOrderRequest(null);

        assertThatNullPointerException().isThrownBy(() -> sut.cancel(request));

        verify(orderRepository, never()).findById(any(OrderId.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @UnitTest
    @TDD
    @Test
    @DisplayName("Should throw NullPointerException when request is null")
    void shouldThrowNullPointerExceptionWhenRequestIsNull() {
        assertThatNullPointerException().isThrownBy(() -> sut.cancel(null));

        verify(orderRepository, never()).findById(any(OrderId.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
    
    private Order createOrderWithStatus(OrderStatus status){
        return Order.createWithStatus(
                new OrderId("123"),
                status
        );
    }
}
