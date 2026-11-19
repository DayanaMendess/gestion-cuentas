package co.sofiamendes.gestioncuentas.service;

import co.sofiamendes.gestioncuentas.dto.CreateClientRequest;
import co.sofiamendes.gestioncuentas.entity.Client;
import co.sofiamendes.gestioncuentas.repository.AccountRepository;
import co.sofiamendes.gestioncuentas.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void rejectsUnderageClient() {
        CreateClientRequest request = new CreateClientRequest(
                "CC",
                "1000000001",
                "Ana",
                "Perez",
                "ana@example.com",
                LocalDate.now(ZoneId.of("America/Bogota")).minusYears(17)
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> clientService.create(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(clientRepository);
    }

    @Test
    void preventsDeletingClientWithAccounts() {
        UUID clientId = UUID.randomUUID();
        Client client = org.mockito.Mockito.mock(Client.class);

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));
        when(accountRepository.existsByClient_Id(clientId))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> clientService.delete(clientId)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(clientRepository, never()).delete(any(Client.class));
    }
}