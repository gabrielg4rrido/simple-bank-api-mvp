package br.com.cwi.bank.notification;

import br.com.cwi.bank.event.TransferCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class TransferNotificationListener {

  private static final Logger log = LoggerFactory.getLogger(TransferNotificationListener.class);

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onTransferCompleted(TransferCompletedEvent event) {
    log.info("Transfer completed: transferId={}, fromAccountId={}, toAccountId={}, amount={}",
      event.transferId(), event.fromAccountId(), event.toAccountId(), event.amount());
  }
}