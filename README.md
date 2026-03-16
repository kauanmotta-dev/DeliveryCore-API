# 🍔 DouraDelivery — Backend API

DouraDelivery é uma API backend desenvolvida em **Java com Spring Boot** que simula o funcionamento de uma plataforma de delivery no estilo marketplace, semelhante a serviços como iFood ou Uber Eats.

O objetivo do projeto é construir um backend robusto com foco em:

* arquitetura bem definida
* consistência transacional
* segurança de domínio
* integridade de dados
* modelagem correta de estados de pedido e pagamento

O projeto é desenvolvido como **estudo avançado de engenharia backend**, simulando desafios reais encontrados em plataformas de delivery.

---

# 🧠 Arquitetura e Conceitos

O backend foi projetado utilizando princípios de engenharia de software aplicados a sistemas transacionais:

* Clean Architecture
* Domain Modeling
* Aggregate Roots
* State Machines
* Consistência transacional
* Idempotência
* Controle de concorrência com Optimistic Locking
* Integração via Webhooks
* APIs REST

---

# 🚚 Modelo de Negócio

O sistema segue o modelo **Marketplace de Delivery**.

Fluxo principal:

Cliente cria pedido
↓
Restaurante recebe e aceita pedido
↓
Restaurante prepara pedido
↓
Entregador aceita entrega
↓
Pedido é entregue ao cliente

---

# 🏗 Estrutura de Domínio

Principais entidades do sistema:

* User
* Customer
* Deliveryman
* Restaurant
* Order
* Payment
* Review

O sistema também utiliza **máquinas de estado** para garantir consistência nos fluxos de pedido e pagamento.

---

# 💳 Segurança Financeira

O núcleo financeiro da aplicação foi projetado para evitar inconsistências comuns em sistemas de pagamento.

Principais mecanismos implementados ou planejados:

* idempotência de webhook
* proteção contra replay attack
* controle de concorrência com optimistic locking
* validação de estados de pagamento
* sincronização entre estados de Order e Payment

---

# 🧰 Tecnologias Utilizadas

## Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate

## Banco de Dados

* PostgreSQL
* MySQL
* MariaDB
* MongoDB
* Redis

## Arquitetura

* REST APIs
* Webhooks
* JWT
* Clean Architecture
* Domain Modeling
* State Machines
* Transaction Management
* Optimistic Locking
* Idempotency

## DevOps

* Git
* GitHub
* Docker
* Docker Compose

## Ferramentas

* Maven
* Postman
* Swagger / OpenAPI

---

# 🚀 Objetivo do Projeto

Este projeto tem como objetivo simular a construção de um backend real de marketplace de delivery, abordando desafios como:

* consistência de pedidos
* pagamentos assíncronos
* concorrência
* escalabilidade de domínio
* segurança de API

Ele também serve como **projeto de portfólio para engenharia backend em Java**.

---

# 🔮 Próximas Evoluções

O projeto continuará evoluindo com a adição de componentes essenciais de um marketplace real:

* catálogo de restaurantes
* menus e produtos
* itens de pedido
* cálculo de taxa de entrega
* busca de restaurantes por localização
* sistema de notificações
* observabilidade (Spring Actuator / Prometheus)
* testes automatizados

---

# 👨‍💻 Autor

Kauan Motta
Backend Java Developer
