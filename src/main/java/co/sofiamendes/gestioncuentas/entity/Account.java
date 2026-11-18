package co.sofiamendes.gestioncuentas.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "account_number", nullable = false, unique = true, length = 10)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "available_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "gmf_exempt", nullable = false)
    private boolean gmfExempt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Account() {
        // JPA lo usa para leer cuentas de la base de datos.
    }

    public Account(Client client, AccountType accountType,
                   String accountNumber, BigDecimal initialBalance,
                   boolean gmfExempt) {
        this.client = client;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.status = AccountStatus.ACTIVE;
        this.balance = initialBalance;
        this.availableBalance = initialBalance;
        this.gmfExempt = gmfExempt;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }


    public void changeStatus(AccountStatus newStatus) {
    this.status = newStatus;
}

public boolean isActive() {
    return status == AccountStatus.ACTIVE;
}

public void deposit(BigDecimal amount) {
    balance = balance.add(amount);
    availableBalance = availableBalance.add(amount);
}

public void withdraw(BigDecimal amount) {
    balance = balance.subtract(amount);
    availableBalance = availableBalance.subtract(amount);
}

    public UUID getId() { return id; }
    public Client getClient() { return client; }
    public AccountType getAccountType() { return accountType; }
    public String getAccountNumber() { return accountNumber; }
    public AccountStatus getStatus() { return status; }
    public BigDecimal getBalance() { return balance; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public boolean isGmfExempt() { return gmfExempt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}