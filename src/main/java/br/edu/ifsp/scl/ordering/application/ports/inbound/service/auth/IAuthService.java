package br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.AuthTokensResponse;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.LoginRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.RefreshRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.RegisterRequest;

public interface IAuthService {
    AuthTokensResponse register(RegisterRequest request);

    AuthTokensResponse login(LoginRequest request);

    AuthTokensResponse refresh(RefreshRequest request);
}
