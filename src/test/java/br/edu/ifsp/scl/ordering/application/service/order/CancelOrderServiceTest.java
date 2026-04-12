package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.CancelOrderService;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class CancelOrderServiceTest {
    CancelOrderService sut = new CancelOrderService();

    @Test
    @DisplayName("Should cancel an order if its status is created")
    void shouldCancelAnOrderIfItsStatusIsCreated() {
        OrderId orderId = new OrderId("123");
        CancelOrderRequest request = new CancelOrderRequest(orderId);

        assertThat(sut.cancel(request)).isEqualTo(true);
    }
}
