package co.sofiamendes.gestioncuentas.repository;

import co.sofiamendes.gestioncuentas.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByClient_Id(UUID clientId);

    List<Account> findByClient_Id(UUID clientId);
}