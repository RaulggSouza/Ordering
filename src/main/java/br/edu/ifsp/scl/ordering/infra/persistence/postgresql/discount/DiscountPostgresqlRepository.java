package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.discount;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.interfaces.DiscountRule;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountId;
import br.edu.ifsp.scl.ordering.domain.valueobject.DiscountTier;
import br.edu.ifsp.scl.ordering.domain.valueobject.MinimumValueDiscountRule;
import br.edu.ifsp.scl.ordering.domain.valueobject.TierDiscountRule;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DiscountPostgresqlRepository implements IDiscountRepository {
    private final SpringDataDiscountRepository repository;

    public DiscountPostgresqlRepository(SpringDataDiscountRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Discount> getAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Discount> findById(DiscountId discountId) {
        Discount discountDomain = toDomain(repository.findById(discountId.value()).orElseThrow());
        return Optional.of(discountDomain);
    }

    private Discount toDomain(DiscountEntity discountEntity) {
        return new Discount(
                new DiscountId(discountEntity.getId()),
                toDomainRule(discountEntity),
                discountEntity.getDiscountType(),
                discountEntity.isActive(),
                discountEntity.getExpiresAt()
        );
    }

    private DiscountRule toDomainRule(DiscountEntity discountEntity) {
        if (discountEntity.getRuleType() == DiscountRuleType.MINIMUM_VALUE) {
            return new MinimumValueDiscountRule(
                    discountEntity.getMinimumValue(),
                    discountEntity.getDiscountValue()
            );
        }

        return new TierDiscountRule(
                discountEntity.getTiers().stream()
                        .map(this::toDomainTier)
                        .toList()
        );
    }

    private DiscountTier toDomainTier(DiscountTierEntity tierEntity) {
        return new DiscountTier(
                tierEntity.getMinimumValue(),
                tierEntity.getMaximumValue(),
                tierEntity.getPercentage()
        );
    }
}
