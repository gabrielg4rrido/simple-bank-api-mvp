package br.com.cwi.bank.exception;

public class AccountBusyException extends RuntimeException {
  public AccountBusyException(String message, Throwable cause) {
    super(message, cause);
  }
}