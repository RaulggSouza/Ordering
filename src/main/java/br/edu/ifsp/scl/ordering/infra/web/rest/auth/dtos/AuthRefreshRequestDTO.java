package br.edu.ifsp.scl.ordering.infra.web.rest.auth.dtos;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos.RefreshRequest;

public record AuthRefreshRequestDTO(
        String accessToken
) {
    public RefreshRequest toRequest() {
        return new RefreshRequest(accessToken);
    }
}
