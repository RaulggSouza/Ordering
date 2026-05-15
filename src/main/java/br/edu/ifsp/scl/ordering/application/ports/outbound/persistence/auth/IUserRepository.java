package br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.auth;

import br.edu.ifsp.scl.ordering.domain.aggregate.User;
import br.edu.ifsp.scl.ordering.domain.valueobject.UserId;

import java.util.Optional;

public interface IUserRepository {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UserId userId);

    UserId save(User user);
}

