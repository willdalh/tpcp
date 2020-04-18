package client;

import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.Socket;

public class ParticipantTest {
    Socket mockSocket;
    Participant participant;
    InputStream scannerInputStream;
    OutputStream scannerOutputStream;

    InputStream mockSocketInputStream;
    OutputStream mockSocketOutputStream;
    @BeforeEach
    void setUp() throws  IOException{
        scannerInputStream = new ByteArrayInputStream("".getBytes());
        participant = new Participant(scannerInputStream);

        mockSocket = Mockito.mock(Socket.class);
        mockSocketInputStream = Mockito.mock(InputStream.class);
        mockSocketOutputStream = Mockito.mock(OutputStream.class);

        Mockito.when(mockSocket.getPort()).thenReturn(1234336567).thenReturn(124);
        Mockito.when(mockSocket.getInputStream()).thenReturn(mockSocketInputStream);
        Mockito.when(mockSocket.getOutputStream()).thenReturn(mockSocketOutputStream);
    }

    @AfterEach
    void tearDown(){
        mockSocket = null;
        participant = null;
    }

    @Test
    void test() throws IOException{
        participant.startConnection(mockSocket);
        scannerInputStream.transferTo(scannerOutputStream);
        PrintWriter printWriter = new PrintWriter(scannerOutputStream);
        printWriter.write("!showlog");
        assertEquals(1234336567, mockSocket.getPort());
        assertEquals(124, mockSocket.getPort());
    }
}
