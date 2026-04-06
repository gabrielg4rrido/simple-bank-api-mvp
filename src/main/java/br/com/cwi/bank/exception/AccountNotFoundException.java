package br.com.cwi.bank.exception;

public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException(String message) {
    super(message);
  }
}