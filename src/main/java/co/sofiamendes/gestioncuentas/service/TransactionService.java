package co.sofiamendes.gestioncuentas.service;

import co.sofiamendes.gestioncuentas.dto.MoneyMovementRequest;
import co.sofiamendes.gestioncuentas.dto.MovementResponse;
import co.sofiamendes.gestioncuentas.dto.TransferRequest;
import co.sofiamendes.gestioncuentas.entity.Account;
import co.sofiamendes.gestioncuentas.entity.AccountStatus;
import co.sofiamendes.gestioncuentas.entity.AccountType;
import co.sofiamendes.gestioncuentas.entity.Movement;
import co.sofiamendes.gestioncuentas.entity.MovementType;
import co.sofiamendes.gestioncuentas.repository.AccountRepository;
import co.sofiamendes.gestioncuentas.repository.MovementRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    public TransactionService(
            AccountRepository accountRepository,
            MovementRepository movementRepository
    ) {
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public Movement deposit(MoneyMovementRequest request) {
        validateAmount(request.amount());

        Account account = findActiveAccountForUpdate(request.accountId());
        account.deposit(request.amount());

        Movement movement = new Movement(
                UUID.randomUUID(),
                account,
                null,
                MovementType.DEPOSIT,
                request.amount(),
                account.getBalance(),
                account.getAvailableBalance()
        );

        return movementRepository.save(movement);
    }

    @Transactional
    public Movement withdraw(MoneyMovementRequest request) {
        validateAmount(request.amount());

        Account account = findActiveAccountForUpdate(request.accountId());
        validateWithdrawal(account, request.amount());
        account.withdraw(request.amount());

        Movement movement = new Movement(
                UUID.randomUUID(),
                account,
                null,
                MovementType.WITHDRAWAL,
                request.amount(),
                account.getBalance(),
                account.getAvailableBalance()
        );

        return movementRepository.save(movement);
    }

    @Transactional
    public List<Movement> transfer(TransferRequest request) {
        validateAmount(request.amount());

        if (request.sourceAccountId().equals(request.destinationAccountId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cuenta origen y destino deben ser diferentes"
            );
        }

        Account firstLocked;
        Account secondLocked;

        if (request.sourceAccountId().toString()
                .compareTo(request.destinationAccountId().toString()) < 0) {
            firstLocked = findAccountForUpdate(request.sourceAccountId());
            secondLocked = findAccountForUpdate(request.destinationAccountId());
        } else {
            firstLocked = findAccountForUpdate(request.destinationAccountId());
            secondLocked = findAccountForUpdate(request.sourceAccountId());
        }

        Account source = firstLocked.getId().equals(request.sourceAccountId())
                ? firstLocked : secondLocked;

        Account destination = firstLocked.getId().equals(request.destinationAccountId())
                ? firstLocked : secondLocked;

        validateActive(source);
        validateActive(destination);
        validateWithdrawal(source, request.amount());

        source.withdraw(request.amount());
        destination.deposit(request.amount());

        UUID operationId = UUID.randomUUID();

        Movement debit = new Movement(
                operationId,
                source,
                destination,
                MovementType.TRANSFER_DEBIT,
                request.amount(),
                source.getBalance(),
                source.getAvailableBalance()
        );

        Movement credit = new Movement(
                operationId,
                destination,
                source,
                MovementType.TRANSFER_CREDIT,
                request.amount(),
                destination.getBalance(),
                destination.getAvailableBalance()
        );

        return movementRepository.saveAll(List.of(debit, credit));
    }

    private Account findActiveAccountForUpdate(UUID accountId) {
        Account account = findAccountForUpdate(accountId);
        validateActive(account);
        return account;
    }

    @Transactional(readOnly = true)
public List<MovementResponse> findByAccountId(UUID accountId) {
    if (!accountRepository.existsById(accountId)) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cuenta no encontrada"
        );
    }

    return movementRepository
            .findByAccount_IdOrderByCreatedAtDesc(accountId)
            .stream()
            .map(MovementResponse::from)
            .toList();
}

    private Account findAccountForUpdate(UUID accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cuenta no encontrada"
                ));
    }

    private void validateActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cuenta debe estar activa para operar"
            );
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El valor debe ser mayor que cero"
            );
        }
    }

    private void validateWithdrawal(Account account, BigDecimal amount) {
        if (account.getAccountType() == AccountType.SAVINGS
                && account.getAvailableBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Saldo insuficiente"
            );
        }
    }
}