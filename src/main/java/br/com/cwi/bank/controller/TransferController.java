package br.com.cwi.bank.controller;

import br.com.cwi.bank.dto.TransferRequest;
import br.com.cwi.bank.dto.TransferResponse;
import br.com.cwi.bank.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfers")
public class TransferController {

  private final TransferService transferService;

  public TransferController(TransferService transferService) {
    this.transferService = transferService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransferResponse create(@RequestBody @Valid TransferRequest request) {
    Long transferId = transferService.transfer(
      request.fromAccountId(),
      request.toAccountId(),
      request.amount()
    );
    return new TransferResponse(transferId);
  }
}