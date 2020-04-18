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
        Socket mockSocket = Mockito.mock(Socket.class);
        Mockito.when(mockSocket.getPort()).thenReturn(1234336567);
    }

    @AfterEach
    void tearDown(){
        mockSocket = null;
        participant = null;
    }

    @Test
    void Test(){
        assertEquals(1234336567, mockSocket.getPort());
    }
}
