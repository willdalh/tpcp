package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/**
 * Class representing a participant in Two-Phase Commit Transaction
 *
 * @author williad
 */
public class Participant {
    final String ADDRESS = "localhost";
    final int PORT = 3000;

    private String address;
    private int port;
    private BufferedReader br;
    private PrintWriter pw;

    /**
     * Constructor with custom address and port
     * @param address IP-address of server
     * @param port port
     */
    public Participant(String address, int port){
        this.address = address;
        this.port = port;
    }

    /**
     * Constructor that sets the default address and port
     */
    public Participant(){
        this.address = ADDRESS;
        this.port = PORT;
    }

    /**
     * Method for starting connection with the server
     */
    public void startConnection(){
        System.out.println("Attempting to connect to server");
        try{
            /* Creating socket connection and objects for communicating with server */
            Socket socket = new Socket(this.address, this.port);
            System.out.println("Connected to server");
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            br = new BufferedReader(isr);
            pw = new PrintWriter(socket.getOutputStream(), true);

            System.out.println(br.readLine());
            Scanner scanner = new Scanner(System.in);

            String scannerInput = scanner.nextLine();
            while(!scannerInput.equals("")){
                // User interaction here
            }

            br.close();
            pw.close();
            socket.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
