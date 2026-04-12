package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    protected ProductEntity() {
    }

    public ProductEntity(String id, String name) {
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