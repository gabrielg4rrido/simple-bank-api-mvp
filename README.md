# Simple Bank API (MVP)

API REST simplificada para um banco digital (teste técnico), construída com **Spring Boot**, **JPA** e **Flyway**.  
Por padrão, roda localmente com **H2** e também suporta execução com **PostgreSQL** via **Docker Compose**.

---

## Requisitos

- Java 21
- Maven
- (Opcional) Docker + Docker Compose para rodar com PostgreSQL

---

## Como rodar (local com H2)

1. Suba a aplicação:

   mvn spring-boot:run

2. Acesse:
    - Aplicação: http://localhost:8080
    - Swagger UI: http://localhost:8080/swagger-ui/index.html  
      (alternativa: http://localhost:8080/swagger-ui.html)
    - OpenAPI JSON: http://localhost:8080/v3/api-docs
    - H2 Console: http://localhost:8080/h2-console


    Observação: o Flyway executa as migrations automaticamente no startup.

---

## Como rodar (Docker com PostgreSQL)

1. Suba Postgres + aplicação:

   docker compose up --build

2. Acesse:
    - Aplicação: http://localhost:8080
    - Swagger UI: http://localhost:8080/swagger-ui/index.html

3. Parar os containers:

   docker compose down

4. Resetar o banco (apagando o volume persistente):

   docker compose down -v


    Observação: ao subir novamente após reset, o Flyway aplicará todas as migrations desde o início.

---

## Endpoints principais

### Listar contas
- GET /accounts

### Criar transferência
- POST /transfers  
  Corpo (exemplo conceitual):
    - fromAccountId
    - toAccountId
    - amount

Respostas esperadas (resumo):
- 201: transferência criada
- 404: conta não encontrada
- 409: saldo insuficiente
- 400: dados inválidos

### Consultar movimentações (paginado)
- GET /accounts/{accountId}/movements

Query params:
- page (default: 0)
- size (default: 20)
- sort (exemplo: createdAt,desc)

---

## Rodando testes

    mvn test

- Rodando teste simulando concorrência com Testcontainers e PostgreSQL (necessita Docker:


    mvn -Dtest=TransferServicePostgresConcurrencyIT test

## Postman Collection
Há uma coleção Postman com envariáveis pré-configuradas para facilitar os testes. 
Importar o arquivo `postman_collection.json` no Postman e ajustar a variável `baseUrl` conforme necessário (ex: http://localhost:8080).

---
