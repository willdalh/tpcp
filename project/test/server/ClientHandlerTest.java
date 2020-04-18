package server;

import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;


class ClentHandlerTest {

    ClientHandler clientHandler;

    @BeforeEach
    void setUp() {
        final Socket socket = mock(Socket.class);
        this.clientHandler = new ClientHandler(socket, 1);
    }

    @Test
    void sendToParticipant(){
        boolean messageSent = clientHandler.sendToParticipant("hei");
        assertTrue(messageSent = true, "Message was not sent");
    }

}
