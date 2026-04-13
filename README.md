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

Com o Docker rodando, ao iniciar a aplicação usando um `src/main/resources/application.properties` baseado no `.example`, o Spring Boot sobe o Postgres automaticamente via Docker Compose (`infra/docker/docker-compose.yml`).

Se preferir subir manualmente:

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

### Seed de mocks (Flyway)

Somente no profile `local`, depois do schema ser criado/atualizado via JPA (`ddl-auto`), o Flyway roda as migrations em `src/main/resources/db/migration` (habilitado por `ordering.flyway.migrate-after-jpa`).

- Seed: `src/main/resources/db/migration/V1__POPULATE_TABLES_WITH_MOCKS.sql`
- Config: `src/main/resources/application.properties` (copiado do `.example`) — inclui `spring.flyway.baseline-on-migrate=true` e `spring.flyway.baseline-version=0` para permitir rodar o Flyway em um schema já criado via JPA e ainda executar a migration `V1__...`

Para rodar o seed novamente em um banco local, a forma mais simples é derrubar o volume:

```bash
docker compose -f infra/docker/docker-compose.yml down -v
```

## Swagger (OpenAPI)

Com a aplicação rodando:

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

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
