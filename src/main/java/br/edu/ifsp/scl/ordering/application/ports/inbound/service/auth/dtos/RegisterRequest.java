package br.edu.ifsp.scl.ordering.application.ports.inbound.service.auth.dtos;

public record RegisterRequest(
        String email,
        String password
) { }

