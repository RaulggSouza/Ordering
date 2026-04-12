package br.edu.ifsp.scl.ordering.infra.web.rest.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.ICancelOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.ICreateOrderService;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.CreateOrderBodyDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.CreateOrderResponseDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.cancel.CancelOrderBodyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final ICreateOrderService createOrderService;
    private final ICancelOrderService cancelOrderService;

    public OrderController(ICreateOrderService createOrderService, ICancelOrderService cancelOrderService) {
        this.createOrderService = createOrderService;
        this.cancelOrderService = cancelOrderService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponseDTO> create(@RequestBody CreateOrderBodyDTO body) {
        OrderId orderId = createOrderService.create(body.toRequest());
        CreateOrderResponseDTO response = CreateOrderResponseDTO.toResponse(orderId);
        return ResponseEntity.created(URI.create("/orders/" + orderId.value()))
                .body(response);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable CancelOrderBodyDTO orderId) {
        cancelOrderService.cancel(orderId.toRequest());
        return ResponseEntity.noContent().build();
    }
}
