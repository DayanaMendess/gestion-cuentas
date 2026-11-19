package co.sofiamendes.gestioncuentas.controller;

import co.sofiamendes.gestioncuentas.entity.Client;
import co.sofiamendes.gestioncuentas.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    @Test
    void listsClientsFromService() {
        List<Client> expectedClients = List.of(mock(Client.class));

        when(clientService.findAll()).thenReturn(expectedClients);

        List<Client> actualClients = clientController.findAll();

        assertSame(expectedClients, actualClients);
        verify(clientService).findAll();
    }

    @Test
    void delegatesDeletionToService() {
        UUID clientId = UUID.randomUUID();

        clientController.delete(clientId);

        verify(clientService).delete(clientId);
    }
}