package br.com.cwi.bank.service;

import br.com.cwi.bank.domain.*;
import br.com.cwi.bank.event.TransferCompletedEvent;
import br.com.cwi.bank.exception.AccountNotFoundException;
import br.com.cwi.bank.exception.InsufficientFundsException;
import br.com.cwi.bank.repository.AccountRepository;
import br.com.cwi.bank.repository.AccountMovementRepository;
import br.com.cwi.bank.repository.TransferRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@Service
public class TransferService {

  private final AccountRepository accountRepository;
  private final TransferRepository transferRepository;
  private final AccountMovementRepository movementRepository;
  private final ApplicationEventPublisher eventPublisher;

  public TransferService(
    AccountRepository accountRepository,
    TransferRepository transferRepository,
    AccountMovementRepository movementRepository,
    ApplicationEventPublisher eventPublisher
  ) {
    this.accountRepository = accountRepository;
    this.transferRepository = transferRepository;
    this.movementRepository = movementRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public Long transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String idempotencyKey) {
    if (fromAccountId.equals(toAccountId)) {
      throw new IllegalArgumentException("A conta destino deve ser diferente da conta de envio.");
    }

    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
      var existing = transferRepository.findByIdempotencyKey(idempotencyKey);
      if (existing.isPresent()) {
        return existing.get().getId();
      }
    } else {
      idempotencyKey = null;
    }

    List<Long> idsToLock = Stream.of(fromAccountId, toAccountId).sorted().toList();

    List<Account> lockedAccounts = accountRepository.findAllByIdInForUpdate(idsToLock);
    if (lockedAccounts.size() != 2) {
      throw new AccountNotFoundException("Conta não encontrada.");
    }

    Account from = lockedAccounts.stream()
      .filter(a -> a.getId().equals(fromAccountId))
      .findFirst()
      .orElseThrow();

    Account to = lockedAccounts.stream()
      .filter(a -> a.getId().equals(toAccountId))
      .findFirst()
      .orElseThrow();

    if (from.getBalance().compareTo(amount) < 0) {
      throw new InsufficientFundsException("Saldo insuficiente.");
    }

    from.debit(amount);
    to.credit(amount);

    Transfer transfer;
    try {
      transfer = transferRepository.save(new Transfer(fromAccountId, toAccountId, amount, idempotencyKey));
    } catch (DataIntegrityViolationException e) {
      if (idempotencyKey != null) {
        return transferRepository.findByIdempotencyKey(idempotencyKey)
          .map(Transfer::getId)
          .orElseThrow(() -> e);
      }
      throw e;
    }

    movementRepository.save(new AccountMovement(fromAccountId, transfer.getId(), MovementType.DEBIT, amount));
    movementRepository.save(new AccountMovement(toAccountId, transfer.getId(), MovementType.CREDIT, amount));

    eventPublisher.publishEvent(new TransferCompletedEvent(
      transfer.getId(), fromAccountId, toAccountId, amount
    ));

    return transfer.getId();
  }

  @Transactional
  public Long transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
    return transfer(fromAccountId, toAccountId, amount, null);
  }
}