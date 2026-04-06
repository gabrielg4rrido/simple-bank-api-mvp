package br.com.cwi.bank.dto;

import java.math.BigDecimal;

public record AccountResponse(Long id, String name, BigDecimal balance) {
}