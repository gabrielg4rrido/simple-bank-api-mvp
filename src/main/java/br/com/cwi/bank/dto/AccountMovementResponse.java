package br.com.cwi.bank.dto;

import br.com.cwi.bank.domain.MovementType;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountMovementResponse(
  Long id,
  Long transferId,
  MovementType movementType,
  BigDecimal amount,
  Instant createdAt
) {}