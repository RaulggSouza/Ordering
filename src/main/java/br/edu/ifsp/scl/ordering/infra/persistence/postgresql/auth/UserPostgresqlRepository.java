package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.auth;

import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.auth.IUserRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.User;
import br.edu.ifsp.scl.ordering.domain.valueobject.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserPostgresqlRepository implements IUserRepository {
    private final SpringDataUserRepository repository;

    public UserPostgresqlRepository(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserPostgresqlRepository::toDomain);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return repository.findById(userId.value()).map(UserPostgresqlRepository::toDomain);
    }

    @Override
    public UserId save(User user) {
        UserEntity saved = repository.save(toEntity(user));
        return new UserId(saved.getId());
    }

    private static User toDomain(UserEntity entity) {
        return new User(
                new UserId(entity.getId()),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getCreatedAt()
        );
    }

    private static UserEntity toEntity(User user) {
        return new UserEntity(
                user.id().value(),
                user.email(),
                user.passwordHash(),
                user.role(),
                user.createdAt()
        );
    }
}

