package co.sofiamendes.gestioncuentas.controller;

import co.sofiamendes.gestioncuentas.dto.CreateClientRequest;
import co.sofiamendes.gestioncuentas.entity.Client;
import co.sofiamendes.gestioncuentas.service.ClientService;
import co.sofiamendes.gestioncuentas.dto.UpdateClientRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client create(@Valid @RequestBody CreateClientRequest request) {
        return clientService.create(request);
    }

    @GetMapping
public List<Client> findAll() {
    return clientService.findAll();
}

@GetMapping("/{id}")
public Client findById(@PathVariable UUID id) {
    return clientService.findById(id);
}

@PutMapping("/{id}")
public Client update(@PathVariable UUID id,
                     @Valid @RequestBody UpdateClientRequest request) {
    return clientService.update(id, request);
}

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable UUID id) {
    clientService.delete(id);
}
}