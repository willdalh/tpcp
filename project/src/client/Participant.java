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
        System.out.println("CLIENT: Attempting to connect to server");
        try{
            /* Creating socket connection and objects for communicating with server */
            Socket socket = new Socket(this.address, this.port);

            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(isr);
            writer = new PrintWriter(socket.getOutputStream(), true);

            String response = this.readFromCoordinatorWithBlocking();
            System.out.println("COORDINATOR: " + response);
            System.out.println("CLIENT: You can now request a query with '!request query'");
            System.out.println("CLIENT: Display the log with '!showlog'");

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
                    /* Also checks for premature instructions */
                    String answerOrPrematureInstructions = this.waitForAnswer();
                    String instructions = this.coordinatorGivingInstructions(answerOrPrematureInstructions);
                    if (instructions.length() > 0){
                        System.out.println("COORDINATOR: " + answerOrPrematureInstructions);
                        System.out.println("CLIENT: PREMATURE INSTRUCTIONS RECEIVED");
                    }
                    else{
                        /* Send YES or NO to coordinator and waits for instructions */
                        response = this.handleParticipantResponse(answerOrPrematureInstructions);

                        /* Checks if coordinator sent instructions */
                        instructions = this.coordinatorGivingInstructions(response);
                    }

                    /* Executes instructions and reports back either with COMMITTED or ROLLBACKED */
                    response = this.executeInstructionsAndReport(instructions);
                    System.out.println("COORDINATOR: " + response);
                    System.out.println("CLIENT: You can now request a query with '!request query'");

                }

                if (scannerInput.length() > 0){
                    /* Participant wishes to see the log */
                    if (scannerInput.equals("!showlog")){
                        System.out.println("CLIENT:\n" + this.getLog());
                    }
                    /* Participant requests a transaction */
                    else if (scannerInput.split(" ")[0].equals("!request")) {
                        this.handleRequest(scannerInput);
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
     * Handles a participant's request for a new transaction
     * @param input input from scanner starting with '!request'
     */
    private void handleRequest(String input){
        if (input.trim().length() > ("!request").length()){
            String request = input.substring(("!request ").length());
            this.requestNewTransaction(request);
        }
        else {
            System.out.println("CLIENT: Invalid format for request");
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
        System.out.println("CLIENT: You requested a transaction with query: " + query);
    }


    /**
     * Waits for response from participant regarding the transaction
     * Will also check if the participant sends premature instructions
     * @return
     */
    private String waitForAnswer(){
        String participantResponse = "";
        String prematureInstructions = "";
        while (!(participantResponse.toUpperCase()).matches("YES|NO")) {
            if (participantResponse.length() > 0){
                System.out.println("CLIENT: Please write either YES or NO");
            }
            participantResponse = (this.readFromScanner()).toUpperCase();
            prematureInstructions = this.readFromCoordinator();
            if (prematureInstructions.length() > 0){
                return prematureInstructions;
            }
        }
        return participantResponse;
    }

    /**
     * Handles the participant's response to the transaction
     * Waits for instructions from coordinator
     *
     * @param participantResponse YES or NO
     */
    private String handleParticipantResponse(String participantResponse){
        this.sendToCoordinator(participantResponse);
        System.out.println("CLIENT: Waiting for instructions from COORDINATOR");

        /* Waits for instructions */
        String response = this.readFromCoordinatorWithBlocking();
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
            System.out.println("CLIENT: Committed");
        }
        else if (instructions.equals("ROLLBACK")){
            this.confirmUndoLog();
            this.sendToCoordinator("ROLLBACKED");
            System.out.println("CLIENT: Rollbacked");
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
            String[] responseSplit = response.split("--");
            if (responseSplit.length == 3){
                String instructions = response.split("--")[2];
                if (instructions == null && !instructions.matches("COMMIT|ROLLBACK")){
                    return "";
                }
                return instructions;
            }
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

    /**
     * Returns the log formatted
     * @return formatted log
     */
    private String getLog(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-------------LOG-------------\n");
        stringBuilder.append(this.log + "\n");
        stringBuilder.append("-----------------------------\n");
        return stringBuilder.toString();
    }
}
