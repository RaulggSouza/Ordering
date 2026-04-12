package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.customer;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.customer.ICustomerRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Customer;
import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerPostgresqlRepository implements ICustomerRepository {
    private final SpringDataCustomerRepository repository;

    public CustomerPostgresqlRepository(SpringDataCustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        return repository.findById(customerId.value())
                .map(customerEntity -> new Customer(
                        new CustomerId(customerEntity.getId()),
                        customerEntity.getName()
                ));
    }
}
