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

        System.out.println("Initiating transaction:\n" + query + "\n");
        messageAll("NEW TRANSACTION--" + query + "--READY TO COMMIT?");

        long start = System.currentTimeMillis();
        long timer = 0L;
        int resCount = 0;
        while(timer / 1000 < 20 && resCount < participants.size()){
            for(ClientHandler party: participants){
                if(party.readFromParticipant().equals("YES")){
                    resCount++;
                    System.out.println("participant nr. " + party.getId() + " is ready to commit\n");
                }else if(party.readFromParticipant().equals("NO")){
                    System.out.println("Transaction cancelled by participant nr. " + party.getId() + "\n");
                    messageAll("TRANSACTION--" + query + "--ABORT");
                    return false;
                }
            }
            timer = new Date().getTime() - start;
        }
        return true;
    }

    private boolean commitTransaction(){

    }

    private void messageAll(String query){
        String message = query;
        for(ClientHandler party: participants){
            party.sendToParticipant(message);
        }
    }

    private boolean success(){

    }

    private boolean rollback(){

    }

    public void start(){
        /*
        wait for request;
        initTransactions();
        commitTransaction();
        success()/rollback();
         */
    }

}
