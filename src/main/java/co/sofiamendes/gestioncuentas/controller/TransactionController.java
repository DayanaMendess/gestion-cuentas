package co.sofiamendes.gestioncuentas.controller;

import co.sofiamendes.gestioncuentas.dto.MoneyMovementRequest;
import co.sofiamendes.gestioncuentas.dto.MovementResponse;
import co.sofiamendes.gestioncuentas.dto.TransferRequest;
import co.sofiamendes.gestioncuentas.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposits")
    @ResponseStatus(HttpStatus.CREATED)
    public MovementResponse deposit(
            @Valid @RequestBody MoneyMovementRequest request) {
        return MovementResponse.from(transactionService.deposit(request));
    }

    @PostMapping("/withdrawals")
    @ResponseStatus(HttpStatus.CREATED)
    public MovementResponse withdraw(
            @Valid @RequestBody MoneyMovementRequest request) {
        return MovementResponse.from(transactionService.withdraw(request));
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MovementResponse> transfer(
            @Valid @RequestBody TransferRequest request) {
        return transactionService.transfer(request).stream()
                .map(MovementResponse::from)
                .toList();
    }

    @GetMapping("/accounts/{accountId}/movements")
    public List<MovementResponse> findByAccountId(
            @PathVariable UUID accountId) {
        return transactionService.findByAccountId(accountId);
    }
}