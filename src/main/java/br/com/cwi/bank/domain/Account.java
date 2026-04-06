package br.com.cwi.bank.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  protected Account() { }

  public Account(String name, BigDecimal balance) {
    this.name = name;
    this.balance = balance;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
  public BigDecimal getBalance() { return balance; }
  public Instant getCreatedAt() { return createdAt; }

  public void credit(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }

  public void debit(BigDecimal amount) {
    this.balance = this.balance.subtract(amount);
  }
}