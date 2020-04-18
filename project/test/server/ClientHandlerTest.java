package server;

import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;


class ClentHandlerTest {

    Socket mockSocket;
    BufferedReader mockReader;
    PrintWriter mockWriter;
    ClientHandler clientHandler;

    @BeforeEach
    void setUp() throws IOException {
        mockSocket = mock(Socket.class);
        mockReader = mock(BufferedReader.class);
        mockWriter = mock(PrintWriter.class);
        when(mockSocket.getInputStream()).thenReturn(mock(InputStream.class));
        when(mockSocket.getOutputStream()).thenReturn(mock(OutputStream.class));
        clientHandler = new ClientHandler(mockSocket, 1);
        clientHandler.reader = mockReader;
        clientHandler.writer = mockWriter;
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
        when(mockReader.ready()).thenReturn(true).thenReturn(false);
        when(mockReader.readLine()).thenReturn("hei");
        assertEquals("hei", clientHandler.readFromParticipant());
        assertEquals("", clientHandler.readFromParticipant());
    }

}
