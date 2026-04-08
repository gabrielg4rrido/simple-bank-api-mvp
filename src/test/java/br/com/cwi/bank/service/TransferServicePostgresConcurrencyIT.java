package br.com.cwi.bank.service;

import br.com.cwi.bank.domain.Account;
import br.com.cwi.bank.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class TransferServicePostgresConcurrencyIT {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
    .withDatabaseName("bank")
    .withUsername("bank")
    .withPassword("bank");

  @DynamicPropertySource
  static void datasourceProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

    registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    registry.add("spring.flyway.enabled", () -> "true");
  }

  @Autowired TransferService transferService;
  @Autowired AccountRepository accountRepository;

  @Test
  @Timeout(120)
  void shouldKeepBalancesConsistentUnderHighConcurrency_onPostgres() throws Exception {
    Account from = accountRepository.save(new Account("From", new BigDecimal("1000.00")));
    Account to = accountRepository.save(new Account("To", new BigDecimal("0.00")));

    int threads = 20;
    int transfersPerThread = 5;
    BigDecimal amount = new BigDecimal("1.00");

    ExecutorService executor = Executors.newFixedThreadPool(threads);
    CyclicBarrier barrier = new CyclicBarrier(threads);
    var futures = new ArrayList<Future<?>>();

    for (int t = 0; t < threads; t++) {
      int threadIndex = t;

      futures.add(executor.submit(() -> {
        barrier.await(60, TimeUnit.SECONDS);

        for (int i = 0; i < transfersPerThread; i++) {
          String key = "pg-" + threadIndex + "-" + i;
          runInNewTransaction(from.getId(), to.getId(), amount, key);
        }
        return null;
      }));
    }

    for (Future<?> f : futures) {
      f.get(120, TimeUnit.SECONDS);
    }

    executor.shutdown();
    assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS), "Executor não terminou a tempo");

    Account fromReloaded = accountRepository.findById(from.getId()).orElseThrow();
    Account toReloaded = accountRepository.findById(to.getId()).orElseThrow();

    assertEquals(0, fromReloaded.getBalance().compareTo(new BigDecimal("900.00")));
    assertEquals(0, toReloaded.getBalance().compareTo(new BigDecimal("100.00")));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  void runInNewTransaction(Long fromId, Long toId, BigDecimal amount, String idempotencyKey) {
    transferService.transfer(fromId, toId, amount, idempotencyKey);
  }
}