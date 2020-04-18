package server;

import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;


class ClentHandlerTest {

    Socket mockSocket;
    ClientHandler clientHandler;

    @BeforeEach
    void setUp() throws IOException {
        mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream()).thenReturn(mock(InputStream.class));
        when(mockSocket.getOutputStream()).thenReturn(mock(OutputStream.class));
        this.clientHandler = new ClientHandler(mockSocket, 1);
    }

    @AfterEach
    void tearDown(){
        mockSocket = null;
        clientHandler = null;
    }

    @Test
    void sendToParticipant(){
        boolean messageSent = clientHandler.sendToParticipant("hei");
        assertTrue(messageSent = true, "Message was not sent");
    }

    @Test
    void readFromParticipant() throws IOException {
        when(clientHandler.reader.readLine()).thenReturn("hei");
        assertEquals("hei", clientHandler.readFromParticipant());
    }

}
