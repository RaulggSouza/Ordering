# Ordering

Projeto desenvolvido no contexto do Trabalho Prático da disciplina **Verificação, Validação e Teste de Software** (IFSP Câmpus São Carlos – Prof. Dr. Lucas Oliveira).

A especificação funcional do sistema (backlog) é mantida nas **Issues** do repositório, em especial:
- `use-case`: objetivos de negócio do sistema;
- `user-story`: regras e cenários (BDD) que detalham os casos de uso.

## Escopo do domínio

O agregado principal é o **Order**. Ele será composto por entidades como **Customer** e **SKUs**. Caso o agregado fique pequeno para o escopo do projeto, podemos considerar inserir outras entidades, como **DistributionCenter**, etc.

### Value Objects (ideias iniciais)
- **Money**: representa valores monetários (moeda, arredondamento e regras associadas).
- **Address**: representa o endereço de entrega e também o endereço do Customer.
- **Discount**: representa descontos associados ao pedido; pode existir mais de um desconto no pedido e eles podem ser acumulativos (respeitando as regras do backlog).

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
