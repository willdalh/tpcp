package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class handles connection requests from clients and creates
 * a new instance of ClientHandler for each new client, and adds it
 * a shared list
 * @author magnubau
 */
public class Server {
    /**
     *This main method goes in loops waiting for new connections to
     * handle
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final int PORTNR = 3000;
        int id = 0;

        ServerSocket tjener = new ServerSocket(PORTNR);
        List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<ClientHandler>());
        System.out.println("Server is running. Waiting for clients...");

        while(true){
            Socket connection = tjener.accept();
            Thread clientHandler = new ClientHandler(connection, id);
            clients.add(clientHandler);
            clientHandler.start();
            System.out.println("client connected.\nClient id: " + id + "\nNumber of clients: " + clients.size() + "\n");
            id++;
        }
    }
}
