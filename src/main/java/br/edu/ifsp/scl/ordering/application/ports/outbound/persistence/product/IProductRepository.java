package br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product;

import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;

import java.util.List;

public interface IProductRepository {
    boolean allExistsByIds(List<ProductId> productIds);
}
