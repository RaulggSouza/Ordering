package br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.create.dtos;

public record CreateOrderItemRequest(String productId, int quantity) {
}
