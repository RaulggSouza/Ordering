package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SpringDataProductInventoryRepository extends JpaRepository<ProductInventoryEntity, String> {
    List<ProductInventoryEntity> findByProductIdIn(Collection<String> productIds);
}
