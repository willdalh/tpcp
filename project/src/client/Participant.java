package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/**
 *
 */
public class Participant {
    final String ADDRESS = "localhost";
    final int PORT = 3000;

    private String address;
    private int port;

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
        this.address = address;
        this.port = PORT;
    }

    /**
     * Method for starting connection with the server
     */
    public void run(){
        try{
            /* Creating socket connection and objects for communicating with server */
            Socket socket = new Socket(this.address, this.port);
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            System.out.println(br.readLine());
            Scanner scanner = new Scanner(System.in);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
