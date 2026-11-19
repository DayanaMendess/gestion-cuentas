package co.sofiamendes.gestioncuentas.dto;

import co.sofiamendes.gestioncuentas.entity.Account;
import co.sofiamendes.gestioncuentas.entity.AccountStatus;
import co.sofiamendes.gestioncuentas.entity.AccountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        UUID clientId,
        AccountType accountType,
        String accountNumber,
        AccountStatus status,
        BigDecimal balance,
        BigDecimal availableBalance,
        boolean gmfExempt,
        Instant createdAt,
        Instant updatedAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getClient().getId(),
                account.getAccountType(),
                account.getAccountNumber(),
                account.getStatus(),
                account.getBalance(),
                account.getAvailableBalance(),
                account.isGmfExempt(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}