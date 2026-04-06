package br.com.cwi.bank.repository;

import br.com.cwi.bank.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
