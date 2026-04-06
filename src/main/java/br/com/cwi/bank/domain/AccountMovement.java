package br.com.cwi.bank.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "account_movements")
public class AccountMovement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="account_id", nullable = false)
  private Long accountId;

  @Column(name="transfer_id", nullable = false)
  private Long transferId;

  @Enumerated(EnumType.STRING)
  @Column(name="movement_type", nullable = false, length = 10)
  private MovementType movementType;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name="created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected AccountMovement() {}

  public AccountMovement(Long accountId, Long transferId, MovementType movementType, BigDecimal amount, Instant createdAt) {
    this.accountId = accountId;
    this.transferId = transferId;
    this.movementType = movementType;
    this.amount = amount;
    this.createdAt = createdAt;
  }

  public Long getId() { return id; }
  public Long getAccountId() { return accountId; }
  public Long getTransferId() { return transferId; }
  public MovementType getMovementType() { return movementType; }
  public BigDecimal getAmount() { return amount; }
  public Instant getCreatedAt() { return createdAt; }
}