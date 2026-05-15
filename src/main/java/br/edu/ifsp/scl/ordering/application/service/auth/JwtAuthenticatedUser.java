package br.edu.ifsp.scl.ordering.application.service.auth;

import br.edu.ifsp.scl.ordering.domain.constant.UserRole;
import br.edu.ifsp.scl.ordering.domain.valueobject.UserId;

public record JwtAuthenticatedUser(
        UserId userId,
        String email,
        UserRole role
) { }

