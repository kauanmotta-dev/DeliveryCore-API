# DouraDelivery Back

API backend em Java/Spring Boot para simular um sistema transacional de delivery, com foco em consistencia de estados, seguranca de pagamento e regras de negocio reais.

Este repositorio faz parte do meu portfolio de backend e esta em evolucao continua.

## Visao Geral

O projeto implementa o nucleo operacional de um app de delivery:

- autenticacao e autorizacao com JWT
- criacao e ciclo de vida de pedidos
- fluxo de pagamento PIX com webhook assinado
- matching de entregador por proximidade
- tracking de localizacao em tempo real
- reviews apos entrega
- auditoria de eventos importantes

O objetivo principal e demonstrar capacidade de modelar dominio, tratar concorrencia e manter invariantes de negocio em um sistema stateful.

## Stack Tecnica

- Java 21
- Spring Boot 3.3.6
- Spring Web
- Spring Security
- Spring Data JPA (Hibernate)
- Spring Validation
- Spring WebSocket (STOMP)
- JWT (jjwt 0.11.5)
- Springdoc OpenAPI (Swagger)
- MySQL
- Maven

## Arquitetura e Decisoes de Engenharia

O codigo foi estruturado em camadas claras (controller, service, repository, model/config), com regras de negocio concentradas no dominio e nos servicos transacionais.

Pontos de arquitetura ja implementados:

- State machine para `Order` e `Payment`
- Invariantes fortes entre estados de pedido e pagamento
- Optimistic locking com `@Version` (pedido e pagamento)
- Idempotencia de pagamento via `idempotencyKey`
- `externalPaymentId` com unicidade no banco
- Webhook com validacao de assinatura HMAC-SHA256
- Expiracao automatica de pagamento por scheduler
- Ownership checks (usuario so acessa recursos que pertencem a ele)
- Seguranca por role (`ADMIN`, `CLIENT`, `DELIVERYMAN`)

## Fluxos de Negocio Implementados

### 1. Fluxo de Pedido

`WAITING_PAYMENT` -> `AVAILABLE` -> `ACCEPTED` -> `IN_DELIVERY` -> `DELIVERED`

Cancelamentos e reembolso tambem fazem parte do fluxo, com regras especificas por estado.

### 2. Fluxo de Pagamento

`PENDING` -> `CONFIRMED`

Estados alternativos:

- `FAILED`
- `EXPIRED`
- `REFUNDED`

### 3. Webhook de Pagamento (PIX)

Processo:

- valida assinatura (`X-Signature`)
- desserializa payload
- confirma pagamento idempotentemente
- promove pedido para estado pagavel/disponivel

### 4. Regras de Cancelamento

- cliente pode cancelar pedido em estados permitidos
- cancelamento pode gerar taxa
- pagamento confirmado pode ser reembolsado parcialmente
- eventos sao auditados

### 5. Entregador e Tracking

- entregador precisa estar online e aprovado para aceitar pedido
- matching por proximidade geografica
- atualizacao de localizacao com throttle minimo de 5 segundos
- validacao de geofence para concluir entrega

### 6. Reviews

- review somente quando o pedido esta `DELIVERED`
- somente participantes do pedido podem avaliar
- uma review por usuario por pedido

## Endpoints Principais

### Auth

- `POST /auth/login`

### Usuarios

- `POST /user/create`
- `GET /user/me`
- `PUT /user/me`
- `PUT /user/me/updatePassword`
- `GET /user` (admin)
- `PATCH /user/{id}/changeStatus` (admin)

### Pedidos

- `POST /order/create/pr%C3%A9-pago` (no codigo a rota literal esta com acento)
- `POST /order/{id}/cancel`
- `POST /order/{id}/accept`
- `POST /order/{id}/cancel-by-deliveryman`
- `POST /order/{id}/start`
- `POST /order/{id}/deliver`
- `GET /order/{id}/history`
- `GET /order/available`
- `GET /order/me/active`
- `GET /order/me`
- `GET /order/my-deliveries`
- `GET /order` (admin)

### Pagamentos

- `POST /payments/order/{orderId}`
- `POST /webhooks/payments/pix`

### Outros Modulos

- `POST /deliveryman/status/online`
- `POST /deliveryman/status/offline`
- `POST /location/order/{orderId}`
- `POST /deliveryman-verification`
- `GET /deliveryman-verification`
- `GET /deliveryman-verification/{id}`
- `POST /deliveryman-verification/{id}/approve`
- `POST /deliveryman-verification/{id}/reject`
- `POST /deliveryman-verification/{id}/suspend`
- `POST /reviews`
- `GET /reviews/users/{userId}/rating`
- `POST /addresses`
- `GET /addresses/me`
- `GET /admin/sla/users`
- `GET /reports/financial`

## Como Executar Localmente

### Requisitos

- JDK 21
- MySQL rodando localmente
- Maven (ou wrapper `mvnw`/`mvnw.cmd`)

### Banco e Configuracao

Configuracao padrao atual:

- host: `localhost`
- porta: `3306`
- database: `railway`
- usuario: `root`
- senha: `password`
- porta da API: `8081`

Variavel opcional para webhook:

- `PAYMENT_WEBHOOK_SECRET` (se nao definida, usa `local-secret`)

### Rodar o projeto

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Build:

```bash
./mvnw clean package
```

## Documentacao da API

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## Seguranca

Implementado:

- autenticacao JWT stateless
- autorizacao por role com `@PreAuthorize`
- ownership checks em recursos sensiveis
- webhook assinado (HMAC)
- tratamento de `OptimisticLockException` com HTTP 409
- autenticacao de handshake para WebSocket

Ainda em evolucao:

- rate limiting global e por endpoint critico
- revogacao de JWT/token blacklist
- estrategias adicionais anti-replay para webhook

## Proximos Passos (Roadmap)

Evolucoes planejadas para chegar ao escopo completo de marketplace:

- agregado de restaurante (owner, endereco, horarios, raio)
- catalogo (menu, categoria, produto)
- carrinho real com snapshot de item/preco
- disponibilidade e despacho avancado de entregadores
- calculo de taxa de entrega por distancia/regra
- busca geoespacial de restaurantes
- notificacoes mais robustas (com persistencia)
- observabilidade (metricas e tracing)
- suite de testes automatizados

## Observacoes de Portfolio

Este projeto esta sendo desenvolvido de forma incremental. O foco atual e consolidar um nucleo robusto de pedidos/pagamentos antes da expansao para marketplace completo.

## Autor

Kauan Motta
