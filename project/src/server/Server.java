package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 3000;

        ServerSocket tjener = new ServerSocket(PORTNR);
        List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<ClientHandler>());
        System.out.println("Server is running. Waiting for clients...");

        while(true){
            Socket connection = tjener.accept();
            Thread clientHandler = new ClientHandler(connection);
            clients.add(clientHandler);
            clientHandler.start();
            System.out.println(clients.size());
        }
    }
}
