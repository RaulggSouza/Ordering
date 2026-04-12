package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_inventory")
public class ProductInventoryEntity {

    @Id
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    protected ProductInventoryEntity() {
    }

    public ProductInventoryEntity(String productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
