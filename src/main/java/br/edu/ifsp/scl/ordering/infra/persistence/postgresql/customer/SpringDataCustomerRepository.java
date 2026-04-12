package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCustomerRepository extends JpaRepository<CustomerEntity, String> {
}
