package br.edu.ifsp.scl.ordering.application.service.discount;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.discount.get_eligible_discounts.dtos.GetEligibleDiscountsRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;
import br.edu.ifsp.scl.ordering.domain.valueobject.MinimumValueDiscountRule;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
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
            nullValues = "NULL",
            value = {
                    "1:1:10,1", // total do pedido: 10
                    "1:10:100-2:10:100,1:2" // total do pedido: 2000
            }
    )
    void shouldReturnAllEligibleDiscounts(String orderProductsInput, String discountsIdsInput) {
        Order order = createOrder(orderProductsInput);
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());
        List<Discount> discounts = createDiscounts();

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(discountRepository.getAll()).thenReturn(discounts);

        List<DiscountId> discountIds = Arrays.stream(discountsIdsInput.split(":"))
                .map(DiscountId::new)
                .toList();

        List<Discount> eligibleDiscounts = sut.getEligibleDiscounts(request).discounts();

        assertThat(eligibleDiscounts)
                .extracting(Discount::getDiscountId)
                .containsExactlyInAnyOrderElementsOf(discountIds);
    }


    private static List<Discount> createDiscounts(){
        return List.of(
                new Discount(new DiscountId("1"), new MinimumValueDiscountRule(1, 1)),
                new Discount(new DiscountId("2"), new MinimumValueDiscountRule(2000, 1))
        );
    }

    private static Order createOrder(String orderProductsInput) {
        List<OrderItem> orderItems = Arrays.stream(orderProductsInput.split("-"))
                .map(productString -> {
                    String[] parts = productString.split(":");
                    String productId = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);
                    return new OrderItem(new ProductId(productId), quantity, price);
                })
                .toList();

        return new Order(new OrderId(UUID.randomUUID().toString()), orderItems);
    }
}
