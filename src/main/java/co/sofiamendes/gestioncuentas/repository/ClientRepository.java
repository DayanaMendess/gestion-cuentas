package co.sofiamendes.gestioncuentas.repository;

import co.sofiamendes.gestioncuentas.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    boolean existsByIdentificationNumber(String identificationNumber);

    Optional<Client> findByIdentificationNumber(String identificationNumber);
}