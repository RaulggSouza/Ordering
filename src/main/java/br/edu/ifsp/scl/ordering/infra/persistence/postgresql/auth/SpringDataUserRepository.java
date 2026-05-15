package br.edu.ifsp.scl.ordering.infra.persistence.postgresql.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);
}

