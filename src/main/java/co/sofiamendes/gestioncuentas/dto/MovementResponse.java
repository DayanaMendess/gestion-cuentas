package co.sofiamendes.gestioncuentas.dto;

import co.sofiamendes.gestioncuentas.entity.Movement;
import co.sofiamendes.gestioncuentas.entity.MovementType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MovementResponse(
        UUID id,
        UUID operationId,
        UUID accountId,
        UUID relatedAccountId,
        MovementType movementType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        BigDecimal availableBalanceAfter,
        Instant createdAt
) {
    public static MovementResponse from(Movement movement) {
        return new MovementResponse(
                movement.getId(),
                movement.getOperationId(),
                movement.getAccount().getId(),
                movement.getRelatedAccount() == null
                        ? null
                        : movement.getRelatedAccount().getId(),
                movement.getMovementType(),
                movement.getAmount(),
                movement.getBalanceAfter(),
                movement.getAvailableBalanceAfter(),
                movement.getCreatedAt()
        );
    }
}