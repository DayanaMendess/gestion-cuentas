package co.sofiamendes.gestioncuentas.service;

import co.sofiamendes.gestioncuentas.dto.CreateAccountRequest;
import co.sofiamendes.gestioncuentas.entity.Account;
import co.sofiamendes.gestioncuentas.entity.AccountType;
import co.sofiamendes.gestioncuentas.entity.Client;
import co.sofiamendes.gestioncuentas.repository.AccountRepository;
import co.sofiamendes.gestioncuentas.repository.ClientRepository;
import co.sofiamendes.gestioncuentas.entity.AccountStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import co.sofiamendes.gestioncuentas.dto.AccountResponse;
import java.util.List;
import java.util.UUID;

import java.security.SecureRandom;
import java.util.Locale;

@Service
public class AccountService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    public AccountService(AccountRepository accountRepository,
                          ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Account create(CreateAccountRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        if (request.accountType() == null
                || request.initialBalance() == null
                || request.initialBalance().signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tipo de cuenta o saldo inicial inválido");
        }

        String prefix = request.accountType() == AccountType.SAVINGS
                ? "53" : "33";
        String accountNumber = generateAccountNumber(prefix);

        Account account = new Account(
                client,
                request.accountType(),
                accountNumber,
                request.initialBalance(),
                request.gmfExempt()
        );

        return accountRepository.save(account);
    }


    @Transactional(readOnly = true)
public List<AccountResponse> findAll() {
    return accountRepository.findAll().stream()
            .map(AccountResponse::from)
            .toList();
}

@Transactional(readOnly = true)
public AccountResponse findById(UUID id) {
    Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Cuenta no encontrada"));

    return AccountResponse.from(account);
}


@Transactional
public AccountResponse changeStatus(UUID id, AccountStatus newStatus) {
    Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Cuenta no encontrada"));

    if (account.getStatus() == AccountStatus.CANCELLED) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Una cuenta cancelada no puede reactivarse");
    }

    if (newStatus == AccountStatus.CANCELLED
            && account.getBalance().signum() != 0) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Solo se puede cancelar una cuenta con saldo cero");
    }

    account.changeStatus(newStatus);
    accountRepository.saveAndFlush(account);
    return AccountResponse.from(account);
}

    private String generateAccountNumber(String prefix) {
        for (int attempt = 0; attempt < 20; attempt++) {
            String number = prefix
                    + String.format(Locale.ROOT, "%08d",
                            RANDOM.nextInt(100_000_000));

            if (!accountRepository.existsByAccountNumber(number)) {
                return number;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "No se pudo generar un número de cuenta");
    }
}