package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import br.edu.ifsp.scl.ordering.testing.tags.UnitTest;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddItemsToOrderServiceTest {
    @Mock
    IOrderRepository orderRepository;

    @InjectMocks
    AddItemsToOrderService sut;

    @TDD
    @UnitTest
    @ParameterizedTest
    @DisplayName("#20 - Should add item into order when all parameters are valid")
    @CsvSource(
        nullValues = "NULL",
        value = {
            "NULL,1:1:100,1:1:100"
        }
    )
    void shouldAddItemsToOrderWhenAllParametersAreValid(String itemsThatAlreadyExistsInOrderInput, String itemsToAddIntoOrderInput, String expectedOrderItemsInput){
        Order order = createOrder("1", itemsThatAlreadyExistsInOrderInput);

        List<OrderItem> orderItemsToAdd = createOrderItems(itemsToAddIntoOrderInput);
        List<OrderItem> expectedOrderItems = createOrderItems(expectedOrderItemsInput);

        AddItemsToOrderRequest request = new AddItemsToOrderRequest(
                order.getOrderId(),
                orderItemsToAdd
        );

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        AddItemsToOrderResponse response = sut.addItemsToOrder(request);

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(orderRepository, times(1)).save(order);

        assertThat(response.orderId()).isEqualTo(order.getOrderId());
        assertThat(response.items()).isEqualTo(expectedOrderItems);
    }


    private static Order createOrder(String orderId, String orderProductsInput) {
        return new Order(
                new OrderId(orderId),
                createOrderItems(orderProductsInput),
                List.of(),
                OrderStatus.CREATED,
                null,
                null
        );
    }

    private static List<OrderItem> createOrderItems(String orderProductsInput) {
        if (orderProductsInput == null || orderProductsInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(orderProductsInput.split("-"))
                .map(productString -> {
                    String[] parts = productString.split(":");
                    String productId = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);

                    return new OrderItem(new ProductId(productId), quantity, price);
                })
                .toList();
    }
}
