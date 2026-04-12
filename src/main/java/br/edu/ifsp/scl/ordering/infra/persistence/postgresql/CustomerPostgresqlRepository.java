package br.edu.ifsp.scl.ordering.infra.persistence.inmemory;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.customer.ICustomerRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Customer;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerPostgresqlRepository implements ICustomerRepository {
    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        return Optional.empty();
    }
}
