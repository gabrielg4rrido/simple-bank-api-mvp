package br.com.cwi.bank.repository;

import br.com.cwi.bank.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
  Optional<Transfer> findByIdempotencyKey(String idempotencyKey);
}
