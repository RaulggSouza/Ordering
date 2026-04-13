package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderItemRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderItemResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.exceptions.InvalidOrderItemQuantityException;
import br.edu.ifsp.scl.ordering.domain.exceptions.ProductNotFoundException;
import br.edu.ifsp.scl.ordering.domain.exceptions.ProductsAlreadyExistInOrderException;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import br.edu.ifsp.scl.ordering.testing.tags.Functional;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import br.edu.ifsp.scl.ordering.testing.tags.UnitTest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddItemsToOrderServiceTest {
    @Mock
    IOrderRepository orderRepository;

    @Mock
    IProductRepository productRepository;

    @InjectMocks
    AddItemsToOrderService sut;

    @TDD
    @UnitTest
    @ParameterizedTest
    @DisplayName("#20 - Should add item into order when all parameters are valid")
    @CsvSource(
        nullValues = "NULL",
        value = {
            "NULL,1:1:100,1:1:100",
            "NULL,1:1:100;2:2:50,1:1:100;2:2:50",
            "1:1:100,2:1:200,1:1:100;2:1:200",
            "1:1:100;2:2:50,3:1:30,1:1:100;2:2:50;3:1:30",
            "NULL,1:2:10;2:3:20;3:1:30,1:2:10;2:3:20;3:1:30",
            "1:1:100,2:2:50;3:1:40,1:1:100;2:2:50;3:1:40"
        }
    )
    void shouldAddItemsToOrderWhenAllParametersAreValid(String itemsThatAlreadyExistsInOrderInput, String itemsToAddIntoOrderInput, String expectedOrderItemsInput){
        Order order = createOrder("1", itemsThatAlreadyExistsInOrderInput);

        List<AddItemsToOrderItemRequest> orderItemsToAdd = createOrderItemsToAdd(itemsToAddIntoOrderInput);
        List<AddItemsToOrderItemResponse> expectedOrderItems = createOrderItemResponse(expectedOrderItemsInput);

        AddItemsToOrderRequest request = new AddItemsToOrderRequest(
                order.getOrderId(),
                orderItemsToAdd
        );

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(productRepository.allExistsByIds(
                orderItemsToAdd.stream()
                        .map(AddItemsToOrderItemRequest::productId)
                        .toList()
        )).thenReturn(true);

        AddItemsToOrderResponse response = sut.addItemsToOrder(request);

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(productRepository, times(1)).allExistsByIds(orderItemsToAdd.stream().map((AddItemsToOrderItemRequest::productId)).toList());
        verify(orderRepository, times(1)).save(order);

        assertThat(response.orderId()).isEqualTo(order.getOrderId());
        assertThat(response.items()).isEqualTo(expectedOrderItems);
    }

    @Functional
    @UnitTest
    @Test
    @DisplayName("#37 - Should throw an error when product does not exist")
    void shouldThrowAnErrorWhenProductDoesNotExist() {
        Order order = createOrder("1", "");

        List<AddItemsToOrderItemRequest> orderItemsToAdd = createOrderItemsToAdd("");

        AddItemsToOrderRequest request = new AddItemsToOrderRequest(
                order.getOrderId(),
                orderItemsToAdd
        );

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(productRepository.allExistsByIds(
                orderItemsToAdd.stream()
                        .map(AddItemsToOrderItemRequest::productId)
                        .toList()
        )).thenReturn(false);

        assertThatThrownBy(() -> sut.addItemsToOrder(request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found");

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(productRepository, times(1)).allExistsByIds(
                orderItemsToAdd.stream()
                        .map(AddItemsToOrderItemRequest::productId)
                        .toList()
        );
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @ParameterizedTest
    @DisplayName("#38 - Should throw an error when product already exists in order")
    @CsvSource(
            nullValues = "NULL",
            value = {
                "1:1:100,1:1:100",
                "1:2:100;2:1:50,1:1:100",
                "1:1:100;2:2:50,2:1:50"
            }
    )
    void shouldThrowAnErrorWhenProductAlreadyExistsInOrder(
            String itemsThatAlreadyExistsInOrderInput,
            String itemsToAddIntoOrderInput
    ) {
        Order order = createOrder("1", itemsThatAlreadyExistsInOrderInput);

        List<AddItemsToOrderItemRequest> orderItemsToAdd = createOrderItemsToAdd(itemsToAddIntoOrderInput);

        AddItemsToOrderRequest request = new AddItemsToOrderRequest(
                order.getOrderId(),
                orderItemsToAdd
        );

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(productRepository.allExistsByIds(
                orderItemsToAdd.stream()
                        .map(AddItemsToOrderItemRequest::productId)
                        .toList()
        )).thenReturn(true);

        assertThatThrownBy(() -> sut.addItemsToOrder(request))
                .isInstanceOf(ProductsAlreadyExistInOrderException.class);

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(productRepository, times(1)).allExistsByIds(
                orderItemsToAdd.stream()
                        .map(AddItemsToOrderItemRequest::productId)
                        .toList()
        );
        verify(orderRepository, never()).save(any());
    }

    @TDD
    @UnitTest
    @ParameterizedTest
    @DisplayName("#39 - Should throw an error when item quantity is less than or equal to zero")
    @CsvSource(
            nullValues = "NULL",
            value = {
                "NULL,1:0:100",
                "NULL,1:-1:100",
                "2:1:50,1:0:100",
                "2:1:50,1:-5:100"
            }
    )
    void shouldThrowAnErrorWhenItemQuantityIsLessThanOrEqualToZero(
            String itemsThatAlreadyExistsInOrderInput,
            String itemsToAddIntoOrderInput
    ) {
        Order order = createOrder("1", itemsThatAlreadyExistsInOrderInput);

        List<AddItemsToOrderItemRequest> orderItemsToAdd = createOrderItemsToAdd(itemsToAddIntoOrderInput);

        AddItemsToOrderRequest request = new AddItemsToOrderRequest(
                order.getOrderId(),
                orderItemsToAdd
        );

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(productRepository.allExistsByIds(
                orderItemsToAdd.stream()
                        .map(AddItemsToOrderItemRequest::productId)
                        .toList()
        )).thenReturn(true);

        assertThatThrownBy(() -> sut.addItemsToOrder(request))
                .isInstanceOf(InvalidOrderItemQuantityException.class);

        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(productRepository, times(1)).allExistsByIds(
                orderItemsToAdd.stream()
                        .map(AddItemsToOrderItemRequest::productId)
                        .toList()
        );
        verify(orderRepository, never()).save(any());
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

                    return new OrderItem(new ProductId(productId), quantity, price);
                })
                .toList();
    }

    private static List<AddItemsToOrderItemRequest> createOrderItemsToAdd(String orderProductsInput) {
        if (orderProductsInput == null || orderProductsInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(orderProductsInput.split(";"))
                .map(productString -> {
                    String[] parts = productString.split(":");
                    String productId = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);

                    return new AddItemsToOrderItemRequest(new ProductId(productId), quantity, price);
                })
                .toList();
    }

    private static List<AddItemsToOrderItemResponse> createOrderItemResponse(String orderProductsInput) {
        if (orderProductsInput == null || orderProductsInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(orderProductsInput.split(";"))
                .map(productString -> {
                    String[] parts = productString.split(":");
                    String productId = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);

                    return new AddItemsToOrderItemResponse(new ProductId(productId), quantity, price);
                })
                .toList();
    }
}
