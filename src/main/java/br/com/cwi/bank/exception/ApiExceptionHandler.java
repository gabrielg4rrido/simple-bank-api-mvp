package br.com.cwi.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

  record ErrorResponse(String message, int status, Instant timestamp) {}

  @ExceptionHandler(InsufficientFundsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleInsufficientFunds(InsufficientFundsException ex) {
    return new ErrorResponse(ex.getMessage(), 409, Instant.now());
  }

  @ExceptionHandler(AccountNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleAccountNotFound(AccountNotFoundException ex) {
    return new ErrorResponse(ex.getMessage(), 404, Instant.now());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBadRequest(IllegalArgumentException ex) {
    return new ErrorResponse(ex.getMessage(), 400, Instant.now());
  }
}