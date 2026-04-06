package br.com.cwi.bank.repository;

import br.com.cwi.bank.domain.AccountMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountMovementRepository extends JpaRepository<AccountMovement, Long> {
  Page<AccountMovement> findByAccountId(Long accountId, Pageable pageable);
}