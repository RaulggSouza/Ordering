package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, String> {
    long countByIdIn(Collection<String> ids);
}
