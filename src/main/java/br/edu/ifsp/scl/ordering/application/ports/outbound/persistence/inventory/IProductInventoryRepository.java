package br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.inventory;

import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

import java.util.List;

public interface IProductInventoryRepository {
    boolean allItemsInStock(List<ProductId> productIds);
}
