package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import utils.*;

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
    private BufferedReader reader;
    private PrintWriter writer;

    private Scanner scanner;

    private String log = "";
    private String undoLog = "";
    private String redoLog = "";

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

            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(isr);
            writer = new PrintWriter(socket.getOutputStream(), true);

            String response = this.readFromCoordinatorWithBlocking();
            System.out.println("COORDINATOR: " + response);

            this.scanner = new Scanner(System.in);
            String scannerInput;

            boolean connected = true;
            while (connected) {
                scannerInput = this.readFromScanner();

                /* Participant wishes to disconnect */
                if (scannerInput.equals("!shutdown")) {
                    this.sendToCoordinator("REQUESTING SHUTDOWN");
                    connected = false;
                }

                response = this.readFromCoordinator();
                /* Check if coordinator is initiating transaction */
                if (this.coordinatorInitiatingTransaction(response)) {
                    String[] responseSplit = response.split("--");
                    System.out.println("COORDINATOR: " + response);

                    String query = responseSplit[1];
                    this.appendToRedoLog(query);

                    /* Check client's response to transaction */
                    String participantResponse = "";
                    while (!participantResponse.matches("YES|NO")) {
                        participantResponse = this.readFromScanner();
                    }
                    response = this.handleParticipantResponse(participantResponse);
                    String instructions = this.coordinatorGivingInstructions(response);
                    response = this.executeInstructionsAndReport(instructions);
                    System.out.println("COORDINATOR: " + response);
                }

                /* Participant requests a transaction */
                if (scannerInput.length() > 0){
                    if (scannerInput.equals("!showlog")){
                        System.out.println("-------------LOG-------------");
                        System.out.println(this.log);
                        System.out.println("-----------------------------");
                    }
                    else if (scannerInput.split(" ")[0].equals("!request")) {
                        String request = scannerInput.substring(("!request ").length());
                        this.requestNewTransaction(request);
                    }
                }
            }

            reader.close();
            writer.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Reads the current string in the scanner buffer
     * @return input from user
     */
    private String readFromScanner(){
        try {
            if (System.in.available() > 0) {
                return scanner.nextLine();
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        return "";
    }

    /**
     * Waits until a string appears in the scanner buffer
     * @return input from user
     */
    private String readFromScannerWithBlocking(){
        return scanner.nextLine();
    }

    /**
     * Requests a new transaction to the coordinator
     * @param query query-request from participant
     */
    private void requestNewTransaction(String query){
        this.sendToCoordinator("REQUESTING NEW TRANSACTION--" + query);
    }

    /**
     * Handles the participant's response to the transaction
     * Waits for instructions from coordinator
     *
     * @param participantResponse YES or NO
     */
    private String handleParticipantResponse(String participantResponse){
        String response;
        this.sendToCoordinator(participantResponse);

        /* Waits for instructions */
        response = this.readFromCoordinatorWithBlocking();
        System.out.println("COORDINATOR: " + response);
        return response;
    }

    /**
     * Executes the instructions from the coordinator, and reports back
     * @param instructions instructions to be executed
     */
    private String executeInstructionsAndReport(String instructions){
        if (instructions.equals("COMMIT")){
            this.confirmRedoLog();
            this.sendToCoordinator("COMMITTED");
            System.out.println("Committed");
        }
        else if (instructions.equals("ROLLBACK")){
            this.confirmUndoLog();
            this.sendToCoordinator("ROLLBACKED");
            System.out.println("Rollbacked");
        }
        /* Waits for response */
        String response = this.readFromCoordinatorWithBlocking();
        return response;
    }

    /**
     * Check instructions from coordinator
     * @param response response from coordinator
     * @return instructions if present, false otherwise
     */
    private String coordinatorGivingInstructions(String response){
        if (response != null){
            String instructions = response.split("--")[2];
            if (instructions == null && !instructions.matches("COMMIT|ROLLBACK")){
                return "";
            }
            return instructions;
        }
        return "";
    }

    /**
     * Appends query to the redo log
     * @param query query to commit
     */
    private void appendToRedoLog(String query){
        this.redoLog += query + "\n";
    }

    /**
     * Sets the log to the redo log
     */
    private void confirmRedoLog(){
        this.undoLog = this.redoLog;
        this.log = this.redoLog;
    }

    /**
     * Sets the log back to the undo log
     */
    private void confirmUndoLog(){
        this.redoLog = this.undoLog;
        this.log = this.undoLog;
    }

    /**
     * Method for checking string to see if coordinator is initiating a transaction
     * @param response
     * @return true if coordinator initiating transaction, false otherwise
     */
    private boolean coordinatorInitiatingTransaction(String response){
        if (response != null) {
            return (response.split("--")[0].equals("NEW TRANSACTION"));
        }
        return false;
    }

    /**
     * Reads from coordinator with blocking
     *
     * @return response from coordinator
     */
    private String readFromCoordinatorWithBlocking(){
        String response = "";
        try {
            response = this.reader.readLine();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        finally {
            return response;
        }
    }

    /**
     * Method for reading messages from coordinator
     *
     * @return response from coordinator
     */
    private String readFromCoordinator(){
        String response = "";
        try {
            if (this.reader.ready()) {
                response = this.reader.readLine();
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        finally {
            return response;
        }
    }

    /**
     * Method for sending message to coordinator
     *
     * @param message message to coordinator
     */
    private void sendToCoordinator(String message){
        if (message != null) {
            this.writer.println(message);
        }
    }
}
