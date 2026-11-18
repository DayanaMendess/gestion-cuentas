package co.sofiamendes.gestioncuentas.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "movements")
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "operation_id", nullable = false)
    private UUID operationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_account_id")
    private Account relatedAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "available_balance_after", nullable = false,
            precision = 19, scale = 2)
    private BigDecimal availableBalanceAfter;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Movement() {
        // JPA lo utiliza al consultar movimientos.
    }

    public Movement(
            UUID operationId,
            Account account,
            Account relatedAccount,
            MovementType movementType,
            BigDecimal amount,
            BigDecimal balanceAfter,
            BigDecimal availableBalanceAfter
    ) {
        this.operationId = operationId;
        this.account = account;
        this.relatedAccount = relatedAccount;
        this.movementType = movementType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.availableBalanceAfter = availableBalanceAfter;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOperationId() { return operationId; }
    public Account getAccount() { return account; }
    public Account getRelatedAccount() { return relatedAccount; }
    public MovementType getMovementType() { return movementType; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public BigDecimal getAvailableBalanceAfter() {
        return availableBalanceAfter;
    }
    public Instant getCreatedAt() { return createdAt; }
}