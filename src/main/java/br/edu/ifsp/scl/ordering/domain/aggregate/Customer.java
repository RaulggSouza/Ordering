package br.edu.ifsp.scl.ordering.domain.aggregate;

import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;

public class Customer {
    private final CustomerId customerId;

    public Customer(CustomerId customerId, String name) {
        this.customerId = customerId;
    }
}
