package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    protected CustomerEntity() {
    }

    public CustomerEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
