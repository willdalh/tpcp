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
    private InputStreamReader readConnection;


    /**
     * Takes in a socket connection and a client id as argument
     * @param connection of type socket
     * @param id client id
     *
     * @author afk, magnubau, williad
     */
    public ClientHandler(Socket connection, int id){
        this.connection = connection;
        this.id = id;

        try {
            readConnection = new InputStreamReader(connection.getInputStream());
            reader = new BufferedReader(readConnection);
            writer = new PrintWriter(connection.getOutputStream(), true);
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }


    /**
     * Receves a message from cordinator and sends it to the participant
     * @param message is the instruction to the participant
     * This methode uses the printwriter function to write the instruction to the participant
     */
    public boolean sendToParticipant(String message) {
        try {
            writer.println(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * reads the response from the participant and return it to the coordinator
     * @return returne the response
     * Uses the methode Buffereader to read the response from the participant. Then the response is returned to the coordinator
     */
    public String readFromParticipant(){
        String line = "";
        try {
            if (this.reader.ready()) {
                line = reader.readLine();
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return line;
    }


    /**
     * This method closes the connection, writer and reader
     */
    public void shutdown(){
        try{
            connection.close();
            reader.close();
            writer.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public String toString(){
        return "ClientHandler for participant with id " + this.getId();
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

}
