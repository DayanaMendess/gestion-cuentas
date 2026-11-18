package co.sofiamendes.gestioncuentas.repository;

import co.sofiamendes.gestioncuentas.entity.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovementRepository extends JpaRepository<Movement, UUID> {

    List<Movement> findByAccount_IdOrderByCreatedAtDesc(UUID accountId);
}