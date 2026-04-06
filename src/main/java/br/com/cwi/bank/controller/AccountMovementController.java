package br.com.cwi.bank.controller;

import br.com.cwi.bank.domain.AccountMovement;
import br.com.cwi.bank.dto.AccountMovementResponse;
import br.com.cwi.bank.exception.AccountNotFoundException;
import br.com.cwi.bank.repository.AccountMovementRepository;
import br.com.cwi.bank.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts/{accountId}/movements")
public class AccountMovementController {

  private final AccountRepository accountRepository;
  private final AccountMovementRepository movementRepository;

  public AccountMovementController(AccountRepository accountRepository,
                                   AccountMovementRepository movementRepository) {
    this.accountRepository = accountRepository;
    this.movementRepository = movementRepository;
  }

  @GetMapping
  public Page<AccountMovementResponse> list(
    @PathVariable Long accountId,
    @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
  ) {
    if (!accountRepository.existsById(accountId)) {
      throw new AccountNotFoundException("Conta não encontrada.");
    }

    Page<AccountMovement> page = movementRepository.findByAccountId(accountId, pageable);

    return page.map(m -> new AccountMovementResponse(
      m.getId(),
      m.getTransferId(),
      m.getMovementType(),
      m.getAmount(),
      m.getCreatedAt()
    ));
  }
}