# Ordering

Projeto desenvolvido no contexto do Trabalho Prático da disciplina **Verificação, Validação e Teste de Software** (IFSP Câmpus São Carlos – Prof. Dr. Lucas Oliveira).

A especificação funcional do sistema (backlog) é mantida nas **Issues** do repositório, em especial:
- `use-case`: objetivos de negócio do sistema;
- `user-story`: regras e cenários (BDD) que detalham os casos de uso.

## Escopo do domínio

O agregado principal é o **Order**. Ele será composto por entidades como **Customer**, **SKUs** e **Discount**. Caso o agregado fique pequeno para o escopo do projeto, podemos considerar inserir outras entidades, como **DistributionCenter**, etc.

### Value Objects (ideias iniciais)
- **Money**: representa valores monetários (moeda, arredondamento e regras associadas).
- **Address**: representa o endereço de entrega e também o endereço do Customer.

## Regras de negócio (alto nível)
- Criar pedido
- Alterar pedido (SKUs/itens, quantidades, etc.)
- Atualizar status (mover para o próximo status válido)
- Calcular valores totais (bruto e líquido)
- Consultar descontos elegíveis dado o estado/valor do pedido
- Aplicar desconto(s) e finalizar/cancelar pedido

## Requisitos técnicos

- **JDK**: usar um JDK compatível com a versão configurada no `pom.xml` (atualmente `25`).
- **Package base** (atual): `br.edu.ifsp.scl.ordering`

## Setup local

### Banco de dados (Postgres via Docker)

Subir o Postgres:

```bash
docker compose -f infra/docker/docker-compose.yml up -d
```

Parar/remover:

```bash
docker compose -f infra/docker/docker-compose.yml down -v
```

### Configuração da aplicação

O arquivo `src/main/resources/application.properties` não é versionado (fica local). Para começar:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### Rodar a aplicação

```bash
mvn spring-boot:run
```

## Testes

Rodar todos:

```bash
mvn test
```

Rodar por tags (JUnit 5):

```bash
mvn test -Dgroups=UnitTest
mvn test -Dgroups=TDD
mvn test -Dgroups=Functional
```
