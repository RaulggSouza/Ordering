package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.CancelOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.dtos.CancelOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
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
    @Test
    @DisplayName("Should cancel an order if its status is created")
    void shouldCancelAnOrderIfItsStatusIsCreated() {
        OrderId orderId = new OrderId("123");
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        Order createdOrder = createOrderWithStatus(OrderStatus.CREATED);
        Order canceledOrder = createOrderWithStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(createdOrder));
        when(orderRepository.save(canceledOrder)).thenReturn(orderId);

        assertThat(sut.cancel(request)).isEqualTo(true);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(canceledOrder);
    }

    private Order createOrderWithStatus(OrderStatus status){
        Address address = new Address(
                "Rua A",
                "456",
                "São Carlos",
                "São Paulo",
                "789");

        List<OrderItem> items = List.of(
                new OrderItem(
                        new ProductId("12"),
                        3
                ),
                new OrderItem(
                        new ProductId("13"),
                        4
                )
        );

        return new Order(
                new OrderId("123"),
                new CustomerId("4"),
                address,
                items,
                status
        );
    }
}
