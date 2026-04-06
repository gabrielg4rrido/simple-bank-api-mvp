package br.com.cwi.bank.service;

import br.com.cwi.bank.domain.*;
import br.com.cwi.bank.event.TransferCompletedEvent;
import br.com.cwi.bank.exception.AccountNotFoundException;
import br.com.cwi.bank.exception.InsufficientFundsException;
import br.com.cwi.bank.repository.AccountMovementRepository;
import br.com.cwi.bank.repository.AccountRepository;
import br.com.cwi.bank.repository.TransferRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

  @Mock private AccountRepository accountRepository;
  @Mock private TransferRepository transferRepository;
  @Mock private AccountMovementRepository movementRepository;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private TransferService transferService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldReturn409WhenInsufficientFunds() {
    Long fromId = 1L;
    Long toId = 2L;
    BigDecimal amount = new BigDecimal("100.00");

    Account from = new Account("From", new BigDecimal("50.00"));
    Account to = new Account("To", new BigDecimal("0.00"));
    setId(from, fromId);
    setId(to, toId);

    when(accountRepository.findAllByIdInForUpdate(List.of(1L, 2L)))
      .thenReturn(List.of(from, to));

    assertThrows(InsufficientFundsException.class,
      () -> transferService.transfer(fromId, toId, amount));

    verify(transferRepository, never()).save(any());
    verify(movementRepository, never()).save(any());
    verify(eventPublisher, never()).publishEvent(any());
  }

  @Test
  void shouldReturn404WhenAccountNotFound() {
    Long fromId = 1L;
    Long toId = 2L;

    Account onlyOne = new Account("From", new BigDecimal("100.00"));
    setId(onlyOne, fromId);

    when(accountRepository.findAllByIdInForUpdate(List.of(1L, 2L)))
      .thenReturn(List.of(onlyOne));

    assertThrows(AccountNotFoundException.class,
      () -> transferService.transfer(fromId, toId, new BigDecimal("10.00")));

    verifyNoInteractions(transferRepository, movementRepository, eventPublisher);
  }

  @Test
  void shouldPersistTransferMovementsAndPublishEventOnSuccess() {
    Long fromId = 1L;
    Long toId = 2L;
    BigDecimal amount = new BigDecimal("10.00");

    Account from = new Account("From", new BigDecimal("100.00"));
    Account to = new Account("To", new BigDecimal("0.00"));
    setId(from, fromId);
    setId(to, toId);

    when(accountRepository.findAllByIdInForUpdate(List.of(1L, 2L)))
      .thenReturn(List.of(from, to));

    Transfer persisted = new Transfer(fromId, toId, amount);
    setId(persisted, 99L);
    when(transferRepository.save(any(Transfer.class))).thenReturn(persisted);

    Long transferId = transferService.transfer(fromId, toId, amount);

    assertEquals(99L, transferId);
    assertEquals(new BigDecimal("90.00"), from.getBalance());
    assertEquals(new BigDecimal("10.00"), to.getBalance());

    verify(transferRepository, times(1)).save(any(Transfer.class));
    verify(movementRepository, times(2)).save(any(AccountMovement.class));

    ArgumentCaptor<TransferCompletedEvent> eventCaptor = ArgumentCaptor.forClass(TransferCompletedEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());

    TransferCompletedEvent evt = eventCaptor.getValue();
    assertEquals(99L, evt.transferId());
    assertEquals(fromId, evt.fromAccountId());
    assertEquals(toId, evt.toAccountId());
    assertEquals(amount, evt.amount());
  }

  private static void setId(Object entity, Long id) {
    try {
      Field f = entity.getClass().getDeclaredField("id");
      f.setAccessible(true);
      f.set(entity, id);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set id via reflection for " + entity.getClass().getSimpleName(), e);
    }
  }
}