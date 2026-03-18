# Ordering
O agregado principal seria o próprio Order. Ele será composto por entidades como Customer e SKUs. Caso considere um agregado pequeno para o escopo do projeto, podemos considerar inserir outras entidades, como DistributionCenter, etc.

Sobre Value objects, pensamos em utilizar:
- Money: Para representar valores monetários com diferentes moedas e/ou regras.
- Address: Para representar o endereço de entrega e também o endereço do Customer
- Discount: Para representar os descontos associados ao pedido. Pode existir mais de um no pedido e são acumulativos.

Regras de negócio relevantes:
- Um pedido não pode ser criado sem SKUs ou com quantidade 0
- O valor total de descontos não pode ultrapassar o valor total dos itens do pedido
- O mesmo tipo de desconto (Ex: promocional) não pode ser aplicado ao pedido. 
- Descontos só podem ser aplicados caso atinjam um valor mínimo de pedido (faixas possíveis de uso)
 - Um pedido não pode ser alterado após ser faturado, finalizado, ou despachado.
-  Um pedido só pode ser cancelado antes de ter sido despachado.

Possíveis casos de uso:
- Criar pedido
- Alterar pedido (SKUs, quantidades, etc)
- Atualizar status (ir para status seguinte, ex: processando -> faturado, faturado -> despachado, etc).
- Calcular valores totais (líquido e bruto)
- Consultar descontos válidos dado valor do pedido
- Finalizar e/ou cancelar pedido.
