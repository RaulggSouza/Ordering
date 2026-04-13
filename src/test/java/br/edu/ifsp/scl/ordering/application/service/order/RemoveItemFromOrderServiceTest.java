package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.IRemoveItemFromOrderService;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
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
public class RemoveItemFromOrderServiceTest {
    @Mock
    IOrderRepository orderRepository;

    @Mock
    IProductRepository productRepository;

    @InjectMocks
    RemoveItemFromOrderService sut;

    @TDD
    @UnitTest
    @ParameterizedTest
    @DisplayName("#45 - Should remove item from order when order is created and has more than one item")
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1:1:100;2:2:50,1,2:2:50",
                    "1:1:100;2:2:50,2,1:1:100",
                    "1:1:100;2:2:50;3:1:30,2,1:1:100;3:1:30"
            }
    )
    void shouldRemoveItemFromOrderWhenOrderIsCreatedAndHasMoreThanOneItem(
            String itemsThatAlreadyExistsInOrderInput,
            String productIdToRemoveInput,
            String expectedOrderItemsInput
    ) {
        Order order = createOrder("1", itemsThatAlreadyExistsInOrderInput);
        ProductId productIdToRemove = new ProductId(productIdToRemoveInput);

        RemoveItemFromOrderRequest request = new RemoveItemFromOrderRequest(
                order.getOrderId(),
                productIdToRemove
        );

        List<RemoveItemFromOrderItemResponse> expectedOrderItems =
                createResponseRemoveItemFromOrder(expectedOrderItemsInput);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(productRepository.existsById(productIdToRemove)).thenReturn(true);

        RemoveItemFromOrderResponse response = sut.removeItemFromOrder(request);

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(productRepository, times(1)).existsById(productIdToRemove);
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

        return Arrays.stream(orderProductsInput.split(";"))
                .map(productString -> {
                    String[] parts = productString.split(":");
                    String productId = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);

                    return new OrderItem(
                            new ProductId(productId),
                            quantity,
                            price
                    );
                })
                .toList();
    }

    private static List<RemoveItemFromOrderItemResponse> createResponseRemoveItemFromOrder(
            String orderProductsInput
    ) {
        if (orderProductsInput == null || orderProductsInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(orderProductsInput.split(";"))
                .map(productString -> {
                    String[] parts = productString.split(":");
                    String productId = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);

                    return new RemoveItemFromOrderItemResponse(
                            new ProductId(productId),
                            quantity,
                            price
                    );
                })
                .toList();
    }
}
