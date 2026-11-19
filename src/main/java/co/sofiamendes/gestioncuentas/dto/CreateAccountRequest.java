package co.sofiamendes.gestioncuentas.dto;

import co.sofiamendes.gestioncuentas.entity.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountRequest(
        @NotNull UUID clientId,
        @NotNull AccountType accountType,
        @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2)
        BigDecimal initialBalance,
        boolean gmfExempt
) {}