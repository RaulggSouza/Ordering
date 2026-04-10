package br.edu.ifsp.scl.ordering.application.service.discount;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        discount = createDiscountWithValue10(discountId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));
    }

    @Test
    @DisplayName("Should add the selected discount to order discount list")
    void shouldAddTheSelectedDiscountToOrderDiscountList() {
        sut.apply(orderId, List.of(discountId));
        assertThat(order.getDiscounts()).contains(discount);
        verify(orderRepository).findById(orderId);
        verify(discountRepository).findById(discountId);
    }

    @Test
    @DisplayName("Should update order total to gross total minus selected discounts")
    void shouldUpdateOrderTotalToGrossTotalMinusSelectedDiscounts() {
        sut.apply(orderId, List.of(discountId));
        assertThat(order.getTotal()).isEqualTo(90.0);
    }

    private Order createOrderWithTotalAs100(OrderId orderId) {
        ProductId productId = new ProductId("product-value-100");
        OrderItem orderItem = new OrderItem(productId, 1, 100.0);
        Order order = new Order(orderId);
        order.addItem(orderItem);
        return order;
    }

    private Discount createDiscountWithValue10(DiscountId discountId) {
        return new Discount(discountId, 10.0);
    }
}
