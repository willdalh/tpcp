package server;

import java.util.ArrayList;
import java.util.Date;

/**
 * This class contains methods that handles two-phase-commit
 * trasnsactions between participants
 */
public class Coordinator {

    private ArrayList<ClientHandler> participants = new ArrayList<>();
    private String tractionStatement;
    private int timeout = 5;
    private ArrayList<Integer> timeOutId = new ArrayList<>();


    /**
     * constructor that sets the participants list
     *
     * @param participants      An ArrayList of ClientHandler objects
     */
    public Coordinator(ArrayList<ClientHandler> participants){
        this.participants = participants;
    }

    /**
     * This method checks if all participants areready to commit a new transaction
     *
     * @param query     The description of the transaction
     * @return          true if all participants are ready to commit. else false
     */
    private boolean initTransaction(String query){
        this.tractionStatement = query;
        System.out.println("Initiating transaction:\n" + this.tractionStatement + "\n");
        messageAll("NEW TRANSACTION--" + this.tractionStatement + "--READY TO COMMIT?");

        long start = System.currentTimeMillis();
        long timer = 0L;
        int resCount = 0;
        String answer = "";
        while(resCount < participants.size()){
            for(ClientHandler party: participants){
                answer = party.readFromParticipant();
                if(answer.equals("YES")){
                    resCount++;
                    System.out.println("participant nr. " + party.getId() + " is ready to commit\n");
                    System.out.println(party.getId());
                    timeOutId.add(party.getId());
                }else if(answer.equals("NO")){
                    System.out.println(party.getId());
                    timeOutId.add(party.getId());
                    System.out.println("Transaction aborted by participant nr. " + party.getId() + "\n");
                    messageAll("TRANSACTION--" + this.tractionStatement + "--ROLLBACK");

                    waitForRollbacked(0);
                    return false;
                }
            }
            timer = (new Date().getTime() - start) / 1000;
            if(timer >= this.timeout){
                System.out.println("Transaction aborted due to timeout\n");
                System.out.println(timeOutId + "id");
                messageAll("TRANSACTION--" + this.tractionStatement + "--ROLLBACK");
                waitForRollbacked(0);
                return false;
            }
        }
        System.out.println("transcation successfully initiated\n");
        return true;
    }

    /**
     * This method instructs all participants to commit, and checks if they
     * are all successfull
     * @return      true if all participants commit successfully. else false
     */
    private boolean commitTransaction(){

        System.out.println("Commiting transaction");
        messageAll("TRANSACTION--" + this.tractionStatement + "--COMMIT");

        long start = System.currentTimeMillis();
        long timer = 0L;
        int resCount = 0;
        String answer = "";
        while(resCount < participants.size()){
            for(ClientHandler party: participants){
                answer = party.readFromParticipant();
                if(answer.equals("COMMITTED")){
                    resCount++;
                    System.out.println("participant nr. " + party.getId() + " has commited\n");
                }else if(answer.equals("ROLLBACKED")) {
                    System.out.println("Transaction aborted by participant nr. " + party.getId() + "\n");
                    messageAll("TRANSACTION--" + this.tractionStatement + "--ROLLBACK");
                    waitForRollbacked(1);
                    return false;
                }
            }
            timer = (new Date().getTime() - start) / 1000;
            if(timer >= this.timeout){
                System.out.println("Transaction aborted due to timeout");
                messageAll("TRANSACTION--" + this.tractionStatement + "--ROLLBACK");
                waitForRollbacked(0);
                return false;
            }
        }
        System.out.println("Transaction commited\n");
        messageAll("TRANSACTION--" + this.tractionStatement + "--SUCCESS");
        return true;
    }

    /**
     * This method sends a message to all participants
     *
     * @param query     the message to be sent
     */
    private void messageAll(String query){
        String message = query;
        for(ClientHandler party: participants){
            party.sendToParticipant(message);
        }
    }

    private boolean waitForRollbacked(int startCount){
        long start = System.currentTimeMillis();
        long timer = 0L;
        int resCount = startCount;
        String answer = "";
        while(resCount < participants.size()){
            for(ClientHandler party: participants){
                answer = party.readFromParticipant();

                if (timeOutId.isEmpty()){
                    messageAll("shutdown");
                }else if(!timeOutId.contains(party.getId())) {
                    System.out.println(party.getId());
                    party.sendToParticipant("YOU HAVE BEEN DISCONECTED DUE TO INACTIVITY");
                    party.sendToParticipant(" TRANSACTION--" + this.tractionStatement + "--shutdown");

                    participants.remove(party);
                    //party.sendToParticipant();
                    System.out.println(participants);
                    System.out.println("----------------");

                }

                if(answer.equals("ROLLBACKED")) {
                    resCount++;
                    System.out.println("participant nr. " + party.getId() + " rolled back\n");



                }
            }
            timer = (new Date().getTime() - start) / 1000;
            if(timer >= this.timeout){

                System.out.println("Stopped waiting for rollback due to timeout");

                /*Does not work
                for (int i = 0; i < participants.size(); i++){
                    if (!timeOutId.contains(participants.get(i).getId())){
                        int party = participants.get(i).getId();
                        System.out.println(party);
                        participants.get(i).sendToParticipant("YOU HAVE BEEN DISCONECTED DUE TO INACTIVITY");
                        participants.get(i).sendToParticipant("shutdown");

                        participants.remove(party);
                        //party.sendToParticipant();
                        System.out.println(participants);
                        System.out.println("----------------");
                    }
                }

 */

                return false;
            }
        }
        messageAll("TRANSACTION--" + this.tractionStatement + "--ROLLBACKED");
        return true;
    }

    /**
     * This method starts a loop that handles incoming transactions
     */

    public void close(){

    }

    public void start(){

        String query = "";
        boolean waiting = true;
        while(true){
            System.out.println("Waiting for transaction request\n");
            while(waiting){
                for(ClientHandler party: participants){
                    query = party.readFromParticipant();
                    if (query.equals("REQUESTING SHUTDOWN")){
                        break;
                    }
                    if(!query.equals("") && query.contains("--")){
                        System.out.println("Got request:\n query:\n" + query);
                        query = query.split("--")[1];
                        System.out.println("query: " + query);

                        waiting = false;
                        break;
                    }else if(!query.equals("")){
                        System.err.println("Recieved invalid transaction request from Client:\n");
                        System.out.println("query: " + query + "\n");
                    }
                }
            }
            if(initTransaction(query)){
                timeOutId.clear();
                commitTransaction();
            }
            waiting = true;
        }
    }

}
