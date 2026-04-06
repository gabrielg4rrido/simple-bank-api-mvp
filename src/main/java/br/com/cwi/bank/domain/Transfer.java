package br.com.cwi.bank.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transfers")
public class Transfer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "from_account_id", nullable = false)
  private Long fromAccountId;

  @Column(name = "to_account_id", nullable = false)
  private Long toAccountId;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  protected Transfer() {}

  public Transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
    this.amount = amount;
  }

  public Long getId() { return id; }
  public Long getFromAccountId() { return fromAccountId; }
  public Long getToAccountId() { return toAccountId; }
  public BigDecimal getAmount() { return amount; }
  public Instant getCreatedAt() { return createdAt; }
}