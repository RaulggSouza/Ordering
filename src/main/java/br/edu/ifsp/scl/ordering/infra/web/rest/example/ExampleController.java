package br.edu.ifsp.scl.ordering.infra.web.rest.example;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.example.IExampleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
public class ExampleController {
    private final IExampleService exampleService;

    public ExampleController(IExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping
    public String get() {
        return "ok";
    }
}

