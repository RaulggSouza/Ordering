package br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.inventory;

import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

import java.util.List;

public interface IProductInventoryRepository {
    List<ProductId> findOutOfStockItems(List<OrderItem> items);
}
