package br.edu.ifsp.scl.ordering.infra.web.rest.auth.dtos;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.LoginRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.RegisterRequest;

public record AuthCredentialsRequestDTO(
        String email,
        String password
) {
    public RegisterRequest toRegisterRequest() {
        return new RegisterRequest(email, password);
    }

    public LoginRequest toLoginRequest() {
        return new LoginRequest(email, password);
    }
}

