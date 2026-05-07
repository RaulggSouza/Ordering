package br.edu.ifsp.scl.ordering.domain.valueobject;


public final class Address {
    final String street;
    final String number;
    final String city;
    final String state;
    final String postalCode;

    public Address(String street, String number, String city, String state, String postalCode) {
        this.street = validateAttribute(street);
        this.number = validateAttribute(number);
        this.city = validateAttribute(city);
        this.state = validateAttribute(state);
        this.postalCode = validateAttribute(postalCode);
    }

    private String validateAttribute(String attribute) {
        if (attribute == null || attribute.isBlank()) throw new IllegalArgumentException("Invalid argument for address");
        return attribute;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }
}
