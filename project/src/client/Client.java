package client;

/**
 * Client with main method
 */
public class Client {
    public static void main(String[] args) {
        Participant participant = new Participant();
        participant.startConnection();
    }
}
