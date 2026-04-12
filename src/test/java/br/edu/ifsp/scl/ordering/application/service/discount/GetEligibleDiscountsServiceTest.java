package br.edu.ifsp.scl.ordering.application.service.discount;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.*;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetEligibleDiscountsServiceTest {
    @Mock
    IOrderRepository orderRepository;

    @Mock
    IDiscountRepository discountRepository;

    @InjectMocks
    GetEligibleDiscountsService sut;

    @TDD
    @DisplayName("#59 - should return all eligible discounts")
    @ParameterizedTest
    @CsvSource(
            value = {
                "1,1:1:100,1", // total do pedido: 100
                "1,1:10:100-2:10:100,1:2", // total do pedido: 2000
                "1,1:100:100,1:2:3", // total do pedido: 10000
                "1,1:100:120,1:2" // total do pedido: 12000
            }
    )
    void shouldReturnAllEligibleDiscounts(String orderId, String orderProductsInput, String discountsIdsInput) {
        Order order = createOrder(orderId, orderProductsInput);
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());
        List<Discount> discounts = createDiscounts();

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(discountRepository.getAll()).thenReturn(discounts);

        List<DiscountId> discountIds = Arrays.stream(discountsIdsInput.split(":"))
                .map(DiscountId::new)
                .toList();

        List<Discount> eligibleDiscounts = sut.getEligibleDiscounts(request).discounts();

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(discountRepository, times(1)).getAll();

        assertThat(eligibleDiscounts)
                .extracting(Discount::getDiscountId)
                .containsExactlyInAnyOrderElementsOf(discountIds);
    }

    @TDD
    @DisplayName("#60 - Should return an empty array if not eligible")
    @ParameterizedTest
    @CsvSource(
        value = {
            "1,1:1:1", // total do pedido: 10
            "1,1:1:35", // total do pedido: 35
        }
    )
    void shouldReturnEmptyArrayIfNotEligible(String orderId, String orderProductsInput) {
        Order order = createOrder(orderId, orderProductsInput);
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());
        List<Discount> discounts = createDiscounts();

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(discountRepository.getAll()).thenReturn(discounts);

        List<Discount> eligibleDiscounts = sut.getEligibleDiscounts(request).discounts();
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(discountRepository, times(1)).getAll();

        assertThat(eligibleDiscounts).isEmpty();
    }


    private static List<Discount> createDiscounts(){
        return List.of(
                new Discount(new DiscountId("1"), new MinimumValueDiscountRule(100, 1)),
                new Discount(new DiscountId("2"), new MinimumValueDiscountRule(2000, 1)),
                new Discount(new DiscountId("3"), new TierDiscountRule(List.of(new DiscountTier(9000, 11000)))),
        new Discount(new DiscountId("3"), new TierDiscountRule(List.of(new DiscountTier(20, 30), new DiscountTier(40, 50))))
        );
    }

    private static Order createOrder(String orderId, String orderProductsInput) {
        List<OrderItem> orderItems = Arrays.stream(orderProductsInput.split("-"))
                .map(productString -> {
                    String[] parts = productString.split(":");
                    String productId = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);
                    return new OrderItem(new ProductId(productId), quantity, price);
                })
                .toList();

        return new Order(new OrderId(orderId), orderItems);
    }
}
