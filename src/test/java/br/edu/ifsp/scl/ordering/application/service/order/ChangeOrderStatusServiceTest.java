package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import br.edu.ifsp.scl.ordering.testing.tags.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
