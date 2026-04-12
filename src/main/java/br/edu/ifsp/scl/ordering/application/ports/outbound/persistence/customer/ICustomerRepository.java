package br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.customer;

import br.edu.ifsp.scl.ordering.domain.aggregate.Customer;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;

import java.util.Optional;

public interface ICustomerRepository {
    Optional<Customer> findById(CustomerId customerId);
}
