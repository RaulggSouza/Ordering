package br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos;

public record LoginRequest(
        String email,
        String password
) { }

