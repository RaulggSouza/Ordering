package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderRepository extends JpaRepository<OrderEntity, String> {
}
