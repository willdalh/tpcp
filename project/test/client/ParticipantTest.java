package client;

import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.net.Socket;

public class ParticipantTest {
    Socket mockSocket;
    Participant participant;
    @BeforeEach
    void setUp(){
        participant = new Participant();
        mockSocket = Mockito.mock(Socket.class);
        Mockito.when(mockSocket.getPort()).thenReturn(1234336567).thenReturn(124);

    }

    @AfterEach
    void tearDown(){
        mockSocket = null;
        participant = null;
    }

    @Test
    void test(){
        participant.startConnection();
        assertEquals(1234336567, mockSocket.getPort());
        assertEquals(124, mockSocket.getPort());
    }
}
