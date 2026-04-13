package br.edu.ifsp.scl.ordering.infra.web.rest.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.IAddItemsToOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.ICancelOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.ICreateOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.IRemoveItemFromOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.IUpdateOrderItemQuantityService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityResponse;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.CreateOrderBodyDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.CreateOrderResponseDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.add_items.AddItemsToOrderRequestDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.add_items.AddItemsToOrderResponseDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.cancel.CancelOrderBodyDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.remove_item.RemoveItemFromOrderResponseDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.update_item_quantity.UpdateOrderItemQuantityRequestDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.update_item_quantity.UpdateOrderItemQuantityResponseDTO;
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
    private final IAddItemsToOrderService addItemsToOrderService;
    private final IUpdateOrderItemQuantityService  updateOrderItemQuantityService;
    private final IRemoveItemFromOrderService removeItemFromOrderService;

    public OrderController(
            ICreateOrderService createOrderService,
            ICancelOrderService cancelOrderService,
            IAddItemsToOrderService addItemsToOrderService,
            IUpdateOrderItemQuantityService updateOrderItemQuantityService,
            IRemoveItemFromOrderService removeItemFromOrderService
    ) {
        this.createOrderService = createOrderService;
        this.cancelOrderService = cancelOrderService;
        this.addItemsToOrderService = addItemsToOrderService;
        this.updateOrderItemQuantityService = updateOrderItemQuantityService;
        this.removeItemFromOrderService = removeItemFromOrderService;
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

    @PatchMapping("/{orderId}/items")
    public ResponseEntity<AddItemsToOrderResponseDTO> addItemsToOrder(
            @PathVariable String orderId,
            @RequestBody AddItemsToOrderRequestDTO body
    ) {
        AddItemsToOrderRequest request = body.toApplicationRequest(new OrderId(orderId));

        AddItemsToOrderResponse response = addItemsToOrderService.addItemsToOrder(request);

        return ResponseEntity.ok(AddItemsToOrderResponseDTO.fromApplicationResponse(response));
    }

    @PatchMapping("/{orderId}/items/{productId}/quantity")
    public ResponseEntity<UpdateOrderItemQuantityResponseDTO> updateOrderItemQuantity(
            @PathVariable String orderId,
            @PathVariable String productId,
            @RequestBody UpdateOrderItemQuantityRequestDTO body
    ) {
        UpdateOrderItemQuantityRequest request = body.toApplicationRequest(
                new OrderId(orderId),
                new ProductId(productId)
        );

        UpdateOrderItemQuantityResponse response =
                updateOrderItemQuantityService.updateOrderItemQuantity(request);

        return ResponseEntity.ok(
                UpdateOrderItemQuantityResponseDTO.fromApplicationResponse(response)
        );
    }

    @DeleteMapping("/{orderId}/items/{productId}")
    public ResponseEntity<RemoveItemFromOrderResponseDTO> removeItemFromOrder(
            @PathVariable String orderId,
            @PathVariable String productId
    ) {
        RemoveItemFromOrderRequest request = new RemoveItemFromOrderRequest(
                new OrderId(orderId),
                new ProductId(productId)
        );

        RemoveItemFromOrderResponse response =
                removeItemFromOrderService.removeItemFromOrder(request);

        return ResponseEntity.ok(
                RemoveItemFromOrderResponseDTO.fromApplicationResponse(response)
        );
    }
}
