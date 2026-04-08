package br.edu.ifsp.scl.ordering.application.service.example;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.example.IExampleService;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.IExampleRepository;
import br.edu.ifsp.scl.ordering.testing.tags.TDD;
import br.edu.ifsp.scl.ordering.testing.tags.UnitTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@TDD
@ExtendWith(MockitoExtension.class)
public class ExampleTest {

    @Mock
    private IExampleRepository repository;

    @InjectMocks
    private IExampleService service;
}
