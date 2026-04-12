package br.edu.ifsp.scl.ordering.infra.persistence.postgresql;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.inventory.IProductInventoryRepository;
import br.edu.ifsp.scl.ordering.domain.entity.OrderItem;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import br.edu.ifsp.scl.ordering.infra.persistence.postgresql.inventory.ProductInventoryEntity;
import br.edu.ifsp.scl.ordering.infra.persistence.postgresql.inventory.SpringDataProductInventoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductInventoryPostgresqlRepository implements IProductInventoryRepository {
    private final SpringDataProductInventoryRepository repository;

    public ProductInventoryPostgresqlRepository(SpringDataProductInventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProductId> findOutOfStockItems(List<OrderItem> items) {
        List<String> productIds = items.stream()
                .map(item -> item.productId().value())
                .distinct()
                .toList();

        List<ProductInventoryEntity> inventoryEntities = repository.findByProductIdIn(productIds);

        Map<String, Integer> stockByProductId = inventoryEntities.stream()
                .collect(Collectors.toMap(
                        ProductInventoryEntity::getProductId,
                        ProductInventoryEntity::getQuantity
                ));

        return items.stream()
                .filter(item -> stockByProductId.getOrDefault(item.productId().value(), 0) < item.quantity())
                .map(OrderItem::productId)
                .distinct()
                .toList();
    }
}
