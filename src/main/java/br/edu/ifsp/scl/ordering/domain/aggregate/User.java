package br.edu.ifsp.scl.ordering.domain.aggregate;

import br.edu.ifsp.scl.ordering.domain.constant.UserRole;
import br.edu.ifsp.scl.ordering.domain.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

public final class User {
    private final UserId id;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final LocalDateTime createdAt;

    public User(UserId id, String email, String passwordHash, UserRole role, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "Null id");
        this.email = requireNonBlank(email, "Null/blank email");
        this.passwordHash = requireNonBlank(passwordHash, "Null/blank passwordHash");
        this.role = Objects.requireNonNull(role, "Null role");
        this.createdAt = Objects.requireNonNull(createdAt, "Null createdAt");
    }

    public static User create(UserId id, String email, String passwordHash, UserRole role) {
        return new User(id, email, passwordHash, role, LocalDateTime.now());
    }

    public UserId id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public UserRole role() {
        return role;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value;
    }
}

