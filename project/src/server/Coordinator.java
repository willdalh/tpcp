package server;


import java.util.ArrayList;
import java.util.Date;

public class Coordinator {

    private ArrayList<ClientHandler> participants = new ArrayList<>();
    private String tractionStatement;
    private String status;

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

        this.tractionStatement = query
        System.out.println("Initiating transaction:\n" + this.tractionStatement + "\n");
        messageAll("NEW TRANSACTION--" + this.tractionStatement + "--READY TO COMMIT?");

        long start = System.currentTimeMillis();
        long timer = 0L;
        int resCount = 0;
        while(resCount < participants.size()){
            for(ClientHandler party: participants){
                if(party.readFromParticipant().equals("YES")){
                    resCount++;
                    System.out.println("participant nr. " + party.getId() + " is ready to commit\n");
                }else if(party.readFromParticipant().equals("NO")){
                    System.out.println("Transaction aborted by participant nr. " + party.getId() + "\n");
                    messageAll("TRANSACTION--" + this.tractionStatement + "--ABORT");
                    return false;
                }
            }
            timer = (new Date().getTime() - start) / 1000;
            if(timer >= 20){
                System.out.println("Transaction aborted due to timeout\n");
                messageAll("TRANSACTION--" + this.tractionStatement + "--ABORT");
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
        while(resCount < participants.size()){
            for(ClientHandler party: participants){
                if(party.readFromParticipant().equals("COMMITTED")){
                    resCount++;
                    System.out.println("participant nr. " + party.getId() + " is ready to commit\n");
                }else if(party.readFromParticipant().equals("ABORTED")){
                    System.out.println("Transaction aborted by participant nr. " + party.getId() + "\n");
                    messageAll("TRANSACTION--" + this.tractionStatement + "--ROLLBACK");
                    return false;
                }
            }
            timer = (new Date().getTime() - start) / 1000;
            if(timer >= 20){
                System.out.println("Transaction aborted due to timeout");
                messageAll("TRANSACTION--" + this.tractionStatement + "--ROLLBACK");
                return false;
            }
        }
        System.out.println("Transaction commited\n");
        messageAll("TRANSACTION--" + this.tractionStatement + "--SUCCESS");
        return true;
    }

    private void messageAll(String query){
        String message = query;
        for(ClientHandler party: participants){
            party.sendToParticipant(message);
        }
    }

    public void start(){
        String query = "";
        boolean waiting = true;
        /*
        wait for request;
        initTransactions();
        commitTransaction();
        success()/rollback();
         */
        while(true){
            while(waiting){
                for(ClientHandler party: participants){
                    query = party.readFromParticipant();
                    if(!query.equals("")){
                        waiting = false;
                    }
                }
            }
            if(initTransaction(query)){
                commitTransaction();
            }
            waiting = true;
        }
    }

}
