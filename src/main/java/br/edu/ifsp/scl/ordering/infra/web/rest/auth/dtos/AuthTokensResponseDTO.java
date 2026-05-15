package br.edu.ifsp.scl.ordering.infra.web.rest.auth.dtos;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.AuthTokensResponse;

public record AuthTokensResponseDTO(
        String accessToken
) {
    public static AuthTokensResponseDTO fromApplication(AuthTokensResponse response) {
        return new AuthTokensResponseDTO(response.accessToken());
    }
}
