## 1) Objetivo do sistema e princípios

### Objetivo
Implementar uma API de banco simplificada com:
- contas (`Account`)
- transferências entre contas (`Transfer`)
- extrato/movimentações (`AccountMovement`)
- persistência consistente de saldo sob concorrência

### Princípios priorizados
1. **Consistência financeira** 
2. **Simplicidade de leitura** 
3. **Evolução incremental** 
4. **Determinismo sob concorrência** 

---

## 2) Estilo de arquitetura: camadas (Controller → Service → Repository)

### Decisão
Adotar o modelo de camadas típico do Spring:
- **Controller**: contrato HTTP, validação de entrada, códigos de status
- **Service**: regra de negócio e transações 
- **Repository**: persistência 


---

## 3) Persistência com JPA + Flyway 

### Decisão
- Usar **Spring Data JPA** para mapeamento e persistência.
- Usar **Flyway** como fonte de verdade do schema, com migrations `V1__...`, `V2__...` etc.

---

## 4) Banco H2 (local) e PostgreSQL (Docker)

### Decisão
- **H2** como default local.
- **PostgreSQL via Docker Compose** para simular ambiente mais real.

---

## 5) Modelagem do domínio e tabelas

### Entidades principais
- `Account`: representa conta com saldo.
- `Transfer`: representa a transferência realizada
- `AccountMovement`: representa um lançamento no extrato (débito/crédito) vinculado a uma transferência.

---

## 6) Caso de uso de trasferência

### Decisão
A transferência é executada como uma operação única dentro de uma transação:
- validar contas
- validar saldo
- debitar/creditar
- persistir `Transfer`
- persistir 2 movimentos (`DEBIT` e `CREDIT`)
- publicar evento “transfer completed”


---

## 7) Concorrência: lock pessimista no banco 

### Decisão
Usar **lock pessimista** ao buscar as duas contas envolvidas:
- `@Lock(LockModeType.PESSIMISTIC_WRITE)`
- query `select a from Account a where a.id in :ids`

---

## 8 Observabilidade (o que foi priorizado / o que faltaria)

### Situação no MVP
O foco do MVP foi consistência e clareza do caso de uso.

### Possível evolução para PRD
- logs estruturados e correlation-id
- Spring Boot Actuator 
- métricas e tracing


---

## 9) Decisões conscientemente NÃO tomadas (para manter o MVP simples)

### Não calcular saldo exclusivamente por ledger
- Ledger-only é uma opção forte para finanças, mas aumenta complexidade do MVP.
- O trade-off escolhido foi saldo materializado com consistência garantida por transação + lock.

---
