package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

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

        ServerSocket server = new ServerSocket(PORTNR);
        List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<ClientHandler>());
        System.out.println("Server is running. Waiting for clients...");
        boolean wait = true;
        while(wait){

            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<Object> waiter = new Callable<Object>() {
                public Object call() throws IOException {
                    return server.accept();
            }
            };
            Future<Object> promise = executor.submit(waiter);
            try {
                Object res = promise.get(5, TimeUnit.SECONDS);
                clients.add(new ClientHandler((Socket)res, id));
                clients.get(clients.size() - 1).start();
                System.out.println("client connected.\nClient id: " + id + "\nNumber of clients: " + clients.size() + "\n");
            } catch (TimeoutException toe) {
                System.out.println("Stoped waiting for clients");
                wait = false;
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                wait = false;
            } catch (ExecutionException ee) {
                ee.printStackTrace();
                wait = false;
            } finally {
                promise.cancel(true);
            }
            id++;
        }
        System.out.println("Donn Morison");
        Coordinator coordinator = new Coordinator();
        server.close();

    }
}
