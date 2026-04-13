package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityItemResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateOrderItemQuantityServiceTest {
    @Mock
    IOrderRepository orderRepository;

    @Mock
    IProductRepository productRepository;

    @InjectMocks
    UpdateOrderItemQuantityService sut;

    @TDD
    @UnitTest
    @ParameterizedTest
    @DisplayName("#40 - Should update item quantity when order is created and item exists")
    @CsvSource(
            nullValues = "NULL",
            value = {
                    "1:1:100,1,2,1:2:100",
                    "1:2:100;2:1:50,1,5,1:5:100;2:1:50",
                    "1:2:100;2:3:50,2,1,1:2:100;2:1:50"
            }
    )
    void shouldUpdateItemQuantityWhenOrderIsCreatedAndItemExists(
            String itemsThatAlreadyExistsInOrderInput,
            String productIdInput,
            Integer newQuantityInput,
            String expectedOrderItemsInput
    ) {
        Order order = createOrder("1", itemsThatAlreadyExistsInOrderInput);
        ProductId productId = new ProductId(productIdInput);

        UpdateOrderItemQuantityRequest request = new UpdateOrderItemQuantityRequest(
                order.getOrderId(),
                productId,
                newQuantityInput
        );

        List<UpdateOrderItemQuantityItemResponse> expectedOrderItems =
                createResponseUpdateOrderItemQuantity(expectedOrderItemsInput);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(productRepository.existsById(productId)).thenReturn(true);

        UpdateOrderItemQuantityResponse response = sut.updateOrderItemQuantity(request);

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

    private static List<UpdateOrderItemQuantityItemResponse> createResponseUpdateOrderItemQuantity(
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

                    return new UpdateOrderItemQuantityItemResponse(
                            new ProductId(productId),
                            quantity,
                            price
                    );
                })
                .toList();
    }
}