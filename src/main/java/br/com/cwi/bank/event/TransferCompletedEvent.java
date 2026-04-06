package br.com.cwi.bank.event;

import java.math.BigDecimal;

public record TransferCompletedEvent(
  Long transferId,
  Long fromAccountId,
  Long toAccountId,
  BigDecimal amount
) {}