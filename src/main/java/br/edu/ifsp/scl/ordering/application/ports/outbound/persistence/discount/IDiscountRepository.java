package br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount;

import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface IDiscountRepository {
    List<Discount> getAll();
}
