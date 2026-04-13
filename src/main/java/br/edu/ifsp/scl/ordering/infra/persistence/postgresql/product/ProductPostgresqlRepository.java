package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.product;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.product.IProductRepository;
import br.edu.ifsp.scl.ordering.domain.valueobject.ProductId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductPostgresqlRepository implements IProductRepository {
    private final SpringDataProductRepository repository;

    public ProductPostgresqlRepository(SpringDataProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean allExistsByIds(List<ProductId> productIds) {
        List<String> ids = productIds.stream()
                .map(ProductId::value)
                .distinct()
                .toList();

        long count = repository.countByIdIn(ids);
        return count == ids.size();
    }

    @Override
    public boolean existsById(ProductId productId) {
        return repository.existsById(productId.value());
    }
}
