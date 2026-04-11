package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderItemRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.customer.ICustomerRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.inventory.IProductInventoryRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Customer;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.exceptions.CustomerNotFoundException;
import br.edu.ifsp.scl.ordering.domain.exceptions.EmptyOrderItemListException;
import br.edu.ifsp.scl.ordering.domain.exceptions.ProductOutOfStockException;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateOrderServiceTest {

    @Mock
    ICustomerRepository customerRepository;

    @Mock
    IProductRepository productRepository;

    @Mock
    IOrderRepository orderRepository;

    @Mock
    IProductInventoryRepository productInventoryRepository;

    @InjectMocks
    CreateOrderService sut;

    @TDD
    @UnitTest
    @Test
    @DisplayName("Should create Order when all parameters are valid")
    void shouldCreateOrderWhenAllParametersAreValid() {
        CreateOrderRequest request = createOrderRequest();
        List<ProductId> products = request.items().stream().map(CreateOrderItemRequest::productId).toList();
        Customer customer = new Customer(request.customerId(), "Peri");
        OrderId mockId = new OrderId("1");

        when(customerRepository.findById(request.customerId())).thenReturn(Optional.of(customer));
        when(productRepository.allExistsByIds(products)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(mockId);
        when(productInventoryRepository.findOutOfStockItems(products)).thenReturn(List.of());

        OrderId result = sut.create(request);

        assertThat(result).isEqualTo(mockId);
        verify(customerRepository, times(1)).findById(request.customerId());
        verify(productRepository, times(1)).allExistsByIds(products);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productInventoryRepository, times(1)).findOutOfStockItems(products);
    }

    private static CreateOrderRequest createOrderRequest() {
        List<CreateOrderItemRequest> orderItems = List.of(
                new CreateOrderItemRequest(
                        new ProductId("12"),
                        3
                ),
                new CreateOrderItemRequest(
                        new ProductId("13"),
                        4
                )
        );
        Address address = new Address(
                "Rua A",
                "123",
                "São Carlos",
                "São Paulo",
                "456"
        );
        return new CreateOrderRequest(
                new CustomerId("123"),
                address,
                orderItems
        );
    }

    @UnitTest
    @Functional
    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer does not exist")
    void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
        CreateOrderRequest request = createOrderRequest();
        when(customerRepository.findById(request.customerId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(CustomerNotFoundException.class);

        verify(customerRepository, times(1)).findById(request.customerId());
        verify(productRepository, never()).allExistsByIds(anyList());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @UnitTest
    @TDD
    @Test
    @DisplayName("Should throw NullPointerException when request is null")
    void shouldThrowNullPointerExceptionWhenRequestIsNull() {
        assertThatNullPointerException().isThrownBy(() -> sut.create(null)).withMessage("Null request");
    }

    @UnitTest
    @Functional
    @Test
    @DisplayName("Should throw NullPointerException when customer is null")
    void shouldThrowNullPointerExceptionWhenCustomerIsNull() {
        Address address = new Address(
                "Rua A",
                "123",
                "São Carlos",
                "São Paulo",
                "456"
        );
        CreateOrderRequest request = new CreateOrderRequest(
                null,
                address,
                List.of(
                        new CreateOrderItemRequest(
                                new ProductId("12"),
                        3
                        ),
                        new CreateOrderItemRequest(
                                new ProductId("13"),
                        4
                        )
                )
        );

        assertThatNullPointerException().isThrownBy(() -> sut.create(request)).withMessage("Customer must not be null");

        verify(customerRepository, never()).findById(any(CustomerId.class));
        verify(productRepository, never()).allExistsByIds(anyList());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @UnitTest
    @Functional
    @Test
    @DisplayName("Should throw EmptyOrderItemListException when items list is empty")
    void shouldThrowEmptyOrderItemListExceptionWhenItemsListIsEmpty() {
        CreateOrderRequest request = new CreateOrderRequest(
                new CustomerId("123"),
                new Address(
                        "Rua A",
                        "123",
                        "São Carlos",
                        "São Paulo",
                        "456"
                ),
                List.of()
        );

        assertThatThrownBy(() -> sut.create(request)).isInstanceOf(EmptyOrderItemListException.class);

        verify(customerRepository, never()).findById(any(CustomerId.class));
        verify(productRepository, never()).allExistsByIds(anyList());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @UnitTest
    @Functional
    @Test
    @DisplayName("Should throw NullPointerException when OrderItems list is null")
    void shouldThrowNullPointerExceptionWhenOrderItemsListIsNull() {
        CreateOrderRequest request = new CreateOrderRequest(
                new CustomerId("123"),
                new Address(
                        "Rua A",
                        "123",
                        "São Carlos",
                        "São Paulo",
                        "456"
                ),
                null
        );

        assertThatNullPointerException().isThrownBy(() -> sut.create(request)).withMessage("OrderItems list must not be null");

        verify(customerRepository, never()).findById(any(CustomerId.class));
        verify(productRepository, never()).allExistsByIds(anyList());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @UnitTest
    @Functional
    @ParameterizedTest
    @CsvSource({
            "0",
            "1",
            "2"
    })
    @DisplayName("Should throw NullPointerException when at least one element of OrderItems list is null")
    void shouldThrowNullPointerExceptionWhenAtLeastOneElementOfOrderItemsListIsNull(int nullIndex) {
        List<CreateOrderItemRequest> items = new ArrayList<>(List.of(
                new CreateOrderItemRequest(
                        new ProductId("12"),
                        3
                ),
                new CreateOrderItemRequest(
                        new ProductId("15"),
                        4
                ),
                new CreateOrderItemRequest(
                        new ProductId("13"),
                        5
                )
        ));

        items.set(nullIndex, null);

        CreateOrderRequest request = new CreateOrderRequest(
                new CustomerId("123"),
                new Address(
                        "Rua A",
                        "123",
                        "São Carlos",
                        "São Paulo",
                        "456"
                ),
                items
        );

        assertThatNullPointerException().isThrownBy(() -> sut.create(request)).withMessage("OrderItems list item must not be null");

        verify(customerRepository, never()).findById(any(CustomerId.class));
        verify(productRepository, never()).allExistsByIds(anyList());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @UnitTest
    @TDD
    @Test
    @DisplayName("Should throw ProductOutOfStockException if at least one OrderItem is out of stock")
    void shouldThrowProductOutOfStockExceptionIfAtLeastOneOrderItemIsOutOfStock() {
        CreateOrderRequest request = createOrderRequest();
        Customer customer = new Customer(request.customerId(), "Peri");
        List<ProductId> productIds = request.items().stream()
                .map(CreateOrderItemRequest::productId)
                .toList();

        when(customerRepository.findById(request.customerId())).thenReturn(Optional.of(customer));
        when(productRepository.allExistsByIds(productIds)).thenReturn(true);
        when(productInventoryRepository.findOutOfStockItems(productIds)).thenReturn(List.of(new ProductId("12")));

        assertThatThrownBy(() -> sut.create(request)).isInstanceOf(ProductOutOfStockException.class);

        verify(customerRepository, times(1)).findById(any(CustomerId.class));
        verify(productRepository, times(1)).allExistsByIds(anyList());
        verify(orderRepository, never()).save(any(Order.class));
        verify(productInventoryRepository, times(1)).findOutOfStockItems(productIds);
    }
}
