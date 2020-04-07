package server;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread{
    private Socket connection = null;
    private int id;

    //Takes in a socket connection and a client id as argument.
    public ClientHandler(Socket connection, int id){
        this.connection = connection;
        this.id = id;
    }

    public void run() {
        try {

            InputStreamReader readConnection = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(readConnection);
            PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);

            writer.println("Connection to the client handler is established");

        }catch (Exception e){
            e.printStackTrace();
        }


    }


}
