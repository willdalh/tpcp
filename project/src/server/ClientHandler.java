package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler{
    private Socket connection = null;
    private int id;
    private BufferedReader reader;
    private PrintWriter writer;
    InputStreamReader readConnection;

    /**
     * Takes in a socket connection and a client id as argument
     * @param connection of type socket
     * @param id client id
     * @author afk
     */
    public ClientHandler(Socket connection, int id){
        this.connection = connection;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Receves a message from cordinator and sends it to the participant
     * @param message is the instruction to the participant
     * This methode uses the printwriter function to write the instruction to the participant
     */
    public void sendToParticipant(String message) {
        try {
            readConnection = new InputStreamReader(connection.getInputStream());
            writer = new PrintWriter(connection.getOutputStream(), true);

            writer.println(message);
            System.out.println("Message is deliverd");

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Connection closed");
    }

    /**
     * reads the response from the participant and return it to the coordinator
     * @return returne the response
     * Uses the methode Buffereader to read the response from the participant. Then the response is returned to the coordinator
     */

    public String readFromParticipant(){
        String line = "";
        try {
            readConnection = new InputStreamReader(connection.getInputStream());
            reader = new BufferedReader(readConnection);
            line = reader.readLine();

            reader.close();

        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return line;
    }

}
