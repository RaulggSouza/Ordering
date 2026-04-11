package br.edu.ifsp.scl.ordering.application.service.example;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.example.IExampleService;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.IExampleRepository;
import org.springframework.stereotype.Service;

@Service
public class ExampleService implements IExampleService {
    private final IExampleRepository exampleRepository;

    public ExampleService(IExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }
}
