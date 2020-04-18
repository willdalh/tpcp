package client;

import java.io.IOException;
import java.net.Socket;

public class Client2 {
    public static void main(String[] args) {
        Participant participant = new Participant();
        try {
            Socket socket = new Socket(participant.getAddress(), participant.getPort());
            participant.startConnection(socket);
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}