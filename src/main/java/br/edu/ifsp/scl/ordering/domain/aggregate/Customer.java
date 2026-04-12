package br.edu.ifsp.scl.ordering.domain.aggregate;

import br.edu.ifsp.scl.ordering.domain.valueobject.CustomerId;

public class Customer {
    private CustomerId customerId;
    private String name;

    public Customer(CustomerId customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
