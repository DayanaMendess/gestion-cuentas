package co.sofiamendes.gestioncuentas.controller;

import co.sofiamendes.gestioncuentas.dto.AccountResponse;
import co.sofiamendes.gestioncuentas.dto.CreateAccountRequest;
import co.sofiamendes.gestioncuentas.entity.AccountStatus;
import co.sofiamendes.gestioncuentas.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(
            @Valid @RequestBody CreateAccountRequest request) {
        return AccountResponse.from(accountService.create(request));
    }

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable UUID id) {
        return accountService.findById(id);
    }

    @PatchMapping("/{id}/status")
    public AccountResponse changeStatus(
            @PathVariable UUID id,
            @RequestParam AccountStatus status) {
        return accountService.changeStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id) {
        accountService.changeStatus(id, AccountStatus.CANCELLED);
    }
}