package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.discount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDiscountRepository extends JpaRepository<DiscountEntity, String> {
}
