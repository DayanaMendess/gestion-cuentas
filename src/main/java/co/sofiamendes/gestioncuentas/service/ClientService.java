package co.sofiamendes.gestioncuentas.service;

import co.sofiamendes.gestioncuentas.dto.CreateClientRequest;
import co.sofiamendes.gestioncuentas.entity.Client;
import co.sofiamendes.gestioncuentas.repository.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import co.sofiamendes.gestioncuentas.dto.UpdateClientRequest; 

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Client create(CreateClientRequest request) {
        LocalDate today = LocalDate.now(ZoneId.of("America/Bogota"));

        if (request.birthDate().plusYears(18).isAfter(today)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El cliente debe ser mayor de edad"
            );
        }

        if (clientRepository.existsByIdentificationNumber(
                request.identificationNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un cliente con ese número de identificación"
            );
        }

        Client client = new Client(
                request.identificationType(),
                request.identificationNumber(),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.birthDate()
        );

        return clientRepository.save(client);
    }

    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
public Client findById(UUID id) {
    return clientRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cliente no encontrado"
            ));
}

@Transactional
public Client update(UUID id, UpdateClientRequest request) {
    LocalDate today = LocalDate.now(ZoneId.of("America/Bogota"));

    if (request.birthDate().plusYears(18).isAfter(today)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El cliente debe ser mayor de edad"
        );
    }

    Client client = findById(id);
    client.update(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.birthDate()
    );

    return client;
}

@Transactional
public void delete(UUID id) {
    Client client = findById(id);
    clientRepository.delete(client);
}
}