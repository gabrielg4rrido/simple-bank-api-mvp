package br.com.cwi.bank.repository;

import br.com.cwi.bank.domain.AccountMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountMovementRepository extends JpaRepository<AccountMovement, Long> {
}
