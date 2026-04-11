package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.ICreateOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderItemRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos.CreateOrderRequest;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.Address;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import br.edu.ifsp.scl.ordering.testing.tags.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@UnitTest
@TDD
@ExtendWith(MockitoExtension.class)
public class CreateOrderServiceTest {

    @Mock
    ICustomerRepository customerRepository;

    @InjectMocks
    CreateOrderService sut;

    @Test
    @DisplayName("shouldCreateOrder")
    void shouldCreateOrder() {
        List<CreateOrderItemRequest> orderItems = List.of(
                new CreateOrderItemRequest(
                        "12",
                        3
                ),
                new CreateOrderItemRequest(
                        "13",
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
        CreateOrderRequest body = new CreateOrderRequest(
                "123",
                address,
                orderItems
        );
        Customer customer = new Customer("123", "Peri");

        when(customerRepository.findById(body.customerId())).thenReturn(Optional.of(customer));

        UUID result = sut.create(body);

        assertThat(result).isNotNull();
        verify(customerRepository, times(1)).findById(body.customerId());
    }
}
