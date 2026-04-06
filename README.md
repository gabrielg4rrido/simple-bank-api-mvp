# Simple Bank API (MVP)

API REST simplificada para um banco digital (teste técnico), construída com **Spring Boot**, **JPA**, **Flyway** e H2.

## Requisitos

- **Java 21**
- **Maven**

## Como rodar

```bash
./mvnw spring-boot:run
```

A aplicação sobe, executa as migrations do Flyway e popula o banco com contas iniciais.

## Swagger (OpenAPI)

- Swagger UI: `http://localhost:8080/swagger-ui.html`  
  (se não abrir, tente `http://localhost:8080/swagger-ui/index.html`)
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Banco de dados (H2)

- H2 Console: `http://localhost:8080/h2-console`

> O projeto usa H2 via JDBC URL definida em `application.properties`.

## Endpoints

### Listar contas

`GET /accounts`

Exemplo:

```bash
curl -s http://localhost:8080/accounts
```

### Criar transferência entre contas

`POST /transfers`

Body:

```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 10.00
}
```

Exemplo:

```bash
curl -s -X POST http://localhost:8080/transfers \
  -H "Content-Type: application/json" \
  -d '{"fromAccountId":1,"toAccountId":2,"amount":10.00}'
```

Respostas esperadas (resumo):
- `201 Created`: transferência criada
- `404 Not Found`: conta não encontrada
- `409 Conflict`: saldo insuficiente
- `400 Bad Request`: dados inválidos

### Consultar movimentações de uma conta (paginado)

`GET /accounts/{accountId}/movements`

Query params:
- `page` (default: 0)
- `size` (default: 20)
- `sort` (ex.: `createdAt,desc`)

Exemplo:

```bash
curl -s "http://localhost:8080/accounts/1/movements?page=0&size=20&sort=createdAt,desc"
```

## Rodando testes

```bash
./mvnw test
```