package br.edu.ifsp.scl.ordering.infra.web.rest.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.IAddItemsToOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.add_items.dtos.AddItemsToOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.IApplyDiscountService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.ICancelOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.cancel.dtos.CancelOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.IChangeOrderStatusService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.change_status.dtos.ChangeOrderStatusResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.ICreateOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.list.IListOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.IRemoveItemFromOrderService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.remove_item.dtos.RemoveItemFromOrderResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.IUpdateOrderItemQuantityService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.update_item_quantity.dtos.UpdateOrderItemQuantityResponse;
import br.edu.ifsp.scl.ordering.domain.valueobject.OrderId;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.add_items.AddItemsToOrderRequestDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.add_items.AddItemsToOrderResponseDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.apply_discount.ApplyDiscountRequestDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.apply_discount.ApplyDiscountResponseDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.change_status.ChangeOrderStatusRequestDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.change_status.ChangeOrderStatusResponseDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.create.CreateOrderBodyDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.create.CreateOrderResponseDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.list_orders.ListOrdersOrderResponseDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.remove_item.RemoveItemFromOrderResponseDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.update_item_quantity.UpdateOrderItemQuantityRequestDTO;
import br.edu.ifsp.scl.ordering.infra.web.rest.order.dtos.update_item_quantity.UpdateOrderItemQuantityResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final ICreateOrderService createOrderService;
    private final IListOrderService listOrderService;
    private final ICancelOrderService cancelOrderService;
    private final IAddItemsToOrderService addItemsToOrderService;
    private final IUpdateOrderItemQuantityService updateOrderItemQuantityService;
    private final IRemoveItemFromOrderService removeItemFromOrderService;
    private final IApplyDiscountService applyDiscountService;
    private final IChangeOrderStatusService changeOrderStatusService;

    public OrderController(
            ICreateOrderService createOrderService,
            IListOrderService listOrderService,
            ICancelOrderService cancelOrderService,
            IAddItemsToOrderService addItemsToOrderService,
            IUpdateOrderItemQuantityService updateOrderItemQuantityService,
            IRemoveItemFromOrderService removeItemFromOrderService,
            IApplyDiscountService applyDiscountService,
            IChangeOrderStatusService changeOrderStatusService
    ) {
        this.createOrderService = createOrderService;
        this.listOrderService = listOrderService;
        this.cancelOrderService = cancelOrderService;
        this.addItemsToOrderService = addItemsToOrderService;
        this.updateOrderItemQuantityService = updateOrderItemQuantityService;
        this.removeItemFromOrderService = removeItemFromOrderService;
        this.applyDiscountService = applyDiscountService;
        this.changeOrderStatusService = changeOrderStatusService;
    }

    @GetMapping
    public ResponseEntity<List<ListOrdersOrderResponseDTO>> list() {
        List<ListOrdersOrderResponseDTO> response = listOrderService.listOrders().stream()
                .map(ListOrdersOrderResponseDTO::fromApplicationResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponseDTO> create(@RequestBody CreateOrderBodyDTO body) {
        OrderId orderId = createOrderService.create(body.toRequest());
        CreateOrderResponseDTO response = CreateOrderResponseDTO.toResponse(orderId);
        return ResponseEntity.created(URI.create("/orders/" + orderId.value()))
                .body(response);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable String orderId) {
        cancelOrderService.cancel(new CancelOrderRequest(new OrderId(orderId)));
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

    @PatchMapping("/{orderId}/discounts")
    public ResponseEntity<ApplyDiscountResponseDTO> applyDiscount(
            @PathVariable String orderId,
            @RequestBody ApplyDiscountRequestDTO body
    ) {
        ApplyDiscountRequest request = body.toApplicationRequest(new OrderId(orderId));

        ApplyDiscountResponse response = applyDiscountService.apply(request);

        return ResponseEntity.ok(
                ApplyDiscountResponseDTO.fromApplicationResponse(response)
        );
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ChangeOrderStatusResponseDTO> changeOrderStatus(
            @PathVariable String orderId,
            @RequestBody ChangeOrderStatusRequestDTO body
    ) {
        ChangeOrderStatusRequest request = body.toApplicationRequest(new OrderId(orderId));

        ChangeOrderStatusResponse response = changeOrderStatusService.change(request);

        return ResponseEntity.ok(
                ChangeOrderStatusResponseDTO.fromApplicationResponse(response)
        );
    }
}
