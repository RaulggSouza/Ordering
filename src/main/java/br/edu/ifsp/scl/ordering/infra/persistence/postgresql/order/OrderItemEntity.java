package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.order;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    protected OrderItemEntity() {
    }

    public OrderItemEntity(String productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public OrderEntity getOrder() {
        return order;
    }
}