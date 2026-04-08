package br.edu.ifsp.scl.ordering.application.service.example;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.example.IExampleService;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.IExampleRepository;

public class ExampleService implements IExampleService {
    public ExampleService(IExampleRepository exampleRepository) {}
}
