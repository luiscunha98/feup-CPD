import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Callable;

public class Game implements Callable<User[]> {

    private final int NUM_PLAYERS = 4;
    private final int NUM_ROUNDS = 3;

    private Server server;
    private User[] players;
    private PrintWriter[] outputs;
    private BufferedReader[] inputs;
    private int currentNumOfPlayers;

    private int id;//debug
    private HashMap<User, Integer> scores = new HashMap<>();
    private List<User> podium = new ArrayList<>();
    private HashMap<User, GAMERESULT> results = new HashMap<>();

    public Game(Server server, int id) {
        this.server = server;
        this.players = new User[NUM_PLAYERS];
        this.outputs = new PrintWriter[NUM_PLAYERS];
        this.inputs = new BufferedReader[NUM_PLAYERS];
        this.currentNumOfPlayers = 0;
        this.id=id;
    }
    

    public synchronized void addPlayer(User player) {
        if (currentNumOfPlayers == NUM_PLAYERS) {
            throw new IllegalStateException("Game full");
        }
        player.setIndexInGame(currentNumOfPlayers); //so that we know the position of the I/O in the vectors
        players[currentNumOfPlayers] = player;
        outputs[currentNumOfPlayers] = player.getOutput();
        inputs[currentNumOfPlayers] = player.getInput();
        currentNumOfPlayers++;
        System.out.println("User: "+ player.getUsername() +" added to a game");
        player.setGameStarted(true);
        player.setLookingForGame(false);
        player.setGame(this);
    }
   
    public boolean isReadyToStart() {
        return currentNumOfPlayers == NUM_PLAYERS;
    }

    public int getId(){//debug
        return this.id;
    }
    public void setInput(BufferedReader input,int index){
        this.inputs[index]=input;
    }
    public void setOutput(PrintWriter output,int index){
        this.outputs[index]=output;
    }


    // --------- Score Dealers ---------

    private void initiateScores() {
        for (User player : this.players) {
            if(player!=null)
            scores.put(player, 0);
        }
    }

    private int pickRandomScore() {
        Random r = new Random();
        int low = 10;
        int high = 30;
        return r.nextInt(high-low) + low;
    }

    // ----------- Builders -----------

    // Give an ordered list of players based on scores
    private void buildPodium() {

        // Sort the entries based on their values
        List<Map.Entry<User, Integer>> sortedEntries = new ArrayList<>(this.scores.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());

        // Clear the list to store the keys in the desired order
        podium.clear();

        // Iterate over the sorted entries and add the keys to the list
        for (Map.Entry<User, Integer> entry : sortedEntries) {
            podium.add(entry.getKey());
        }
    }

    // Match game score of each player with results overall
    private void buildResult(List<User> podium) {
        this.results.put(podium.get(0), GAMERESULT.FIRST);
        this.results.put(podium.get(1), GAMERESULT.SECOND);
        this.results.put(podium.get(2), GAMERESULT.THIRD);
        this.results.put(podium.get(3), GAMERESULT.FORTH);
    }

    // ----------- Getters -----------

    public User[] getPlayers() {
        return players;
    }
    public int getNumPlayers(){
        return this.currentNumOfPlayers;
    }
    public int getNumMaxPlayers(){
        return this.NUM_PLAYERS;
    }

    public HashMap<User, GAMERESULT>  getResults() {
        return this.results;
    }

    // -------- Communication --------

    private void messageEveryone(String message) {
        for (PrintWriter output : outputs) {
            if(output!=null)
                output.println(message);
        }
    }
    


    // ------------- Main -------------

    @Override
    public User[] call() throws Exception{
        try{
        int interval=1000;
        int chances;
        boolean callOff=false;
        //Game has officially started(no more users allowed from the waiting queue)

        for(User user: players){
            chances=0;
            while(!server.isPlayerConnected(user) && chances<10){
                //try to reconnect
                System.out.println("Waiting for "+ user.getUsername() + " to reconnect (Chance num "+chances+")...");
                chances++;
                Thread.sleep(interval);  
            }
            if(chances==10){
                callOff=true;
                int missingPlayerInd=user.getIndexInGame();
                this.players[missingPlayerInd]=null;
                this.outputs[missingPlayerInd]=null;//to avoid errors in messaging everyone
            }

            this.server.removeActiveUser(user.getUsername());
        }

        if(!callOff){
            //once the game starts disconnected users will not be waited
            messageEveryone("Game Started");

            initiateScores();

            // For each round
            for (int i = 0; i < NUM_ROUNDS; i++) {

                messageEveryone("Round " + (1 + i));

                // Give players random scores
                for (User player : this.players) {
                    int randomScore = pickRandomScore();
                    int nowScore = this.scores.get(player);
                    this.scores.replace(player, nowScore + randomScore);
                    
                }

                buildPodium();

            }

            buildResult(podium);

            // Show game results
            for (User player : players) {
                messageEveryone(player.getUsername() + " - " + results.get(player));
            }
            
            System.out.println();

            for (User player : players) {
                player.earnPoints(results.get(player));
            }

        }
        else{
            System.out.println("The Game has been called off");
            messageEveryone("Game canceled because of disconnected players \nYou'll be redirected to the menu");
        }
        }catch(RuntimeException e){
            System.out.println("Error"+ e);
        }finally{
            this.server.cleanDoneGame(this);
        }
        return this.players; 
    }
}
