package br.edu.ifsp.scl.ordering.domain.valueobject;

public record Address(
        String street,
        String number,
        String city,
        String state,
        String postalCode
) {}
