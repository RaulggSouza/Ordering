package br.edu.ifsp.scl.ordering.application.service.discount;

import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetEligibleDiscountsTest {
    @Mock
    IOrderRepository orderRepository;

    @Mock
    IDiscountRepository discountRepository;

    @InjectMocks
    GetEligibleDiscounts sut;

    @TDD
    @DisplayName("#59 - should return all eligible discounts")
    @ParameterizedTest
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1:1", "1"
            }
    )
    void shouldReturnAllEligibleDiscounts(String orderProductsInput, String discountsIdsInput) {
        Order order = createOrder(orderProductsInput);
        GetEligibleDiscountsRequest request = new GetEligibleDiscountsRequest(order.getOrderId());
        List<Discount> discounts = createDiscounts();

        when(orderRepository.findById(order.getOrderId())).thenReturn(order);
        when(discountRepository.getAll()).thenReturn(discounts);

        List<DiscountId> discountIds = Arrays.stream(discountsIdsInput.split(":"))
                .map(DiscountId::new)
                .toList();

        List<Discount> eligibleDiscounts = sut.getEligibleDiscounts(request);

        assertThat(eligibleDiscounts)
                .extracting(Discount::getDiscountId)
                .containsExactlyInAnyOrderElementsOf(discountIds);
    }
}
