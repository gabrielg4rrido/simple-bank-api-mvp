package br.com.cwi.bank.controller;

import br.com.cwi.bank.dto.AccountResponse;
import br.com.cwi.bank.repository.AccountRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  private final AccountRepository accountRepository;

  public AccountController(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @GetMapping
  public List<AccountResponse> getAll() {
    return accountRepository.findAll().stream()
        .map(account -> new AccountResponse(account.getId(), account.getName(), account.getBalance()))
        .toList();
  }
}
