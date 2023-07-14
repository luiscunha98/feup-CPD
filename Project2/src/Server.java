import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
public class Server {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private ExecutorService receptionPool;

    private List<Game> gameList;
    private Queue<User> waitingQueue;
    private Queue<Future<User[]>> futures;
    private Queue<User> usersToDealWith;

    private int maxConcurrentGames;
    private int maxConcurrentConnections;
    private HashMap<String,User> activeUsers;
    
    private Integer numFullGames;
    private Integer numStartedGames;
    private UserManager userManager;
    private String usersFilePath = "../data/Credentials.txt";

    public Server(int port, int maxConcurrentGames,int maxConcurrentConnections) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.threadPool=Executors.newFixedThreadPool(maxConcurrentGames);//pool that take cares of authentication,queue managing 
            //and ultimately games 
            this.receptionPool=Executors.newFixedThreadPool(maxConcurrentConnections);//pool that takes care of accepting clients

            this.gameList = new ArrayList<>(); //active games
            this.waitingQueue=new LinkedList<>(); //users waiting to be assigned a game
            this.futures=new LinkedList<>(); //results of the game threads
            this.usersToDealWith=new LinkedList<>(); 
            this.activeUsers=new HashMap<>(); //already active users that are waiting to take part to game
            this.numFullGames=0;
            this.numStartedGames=0;
            
            this.maxConcurrentGames = maxConcurrentGames;
            this.userManager = new UserManager(usersFilePath);

          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1)
            return;

        int port = Integer.parseInt(args[0]);

        Server server = new Server(port, 5,2);

        server.start();
    }
    public ServerSocket getServerSocket(){
        return this.serverSocket;
    }
    public UserManager getManager(){
        return this.userManager;
    }
    public void start() {
        while (true) {

                SocketReception reception=new SocketReception(this);
                receptionPool.execute(reception);

  
                synchronized(futures){
                    if(futures.size()>0){
                        Iterator<Future<User[]>> iteratorFutures = futures.iterator();
                        while (iteratorFutures.hasNext()){ 
                            //check if the game thread has terminated
                            Future<User[]> future=iteratorFutures.next();
                            if(future.isDone()){
                                try{
                                //if it has add to the queue of users to deal with (meaning show menu etc)
                                //(Games implemented as Callables that return a List of Users)
                                User[] users= future.get();
                                for(User user: users){
                                    if(user!=null){
                                    addUserToDealWith(user);
                                    }
                                }
                                }catch(InterruptedException e){
                                    System.err.println("Task was interrupted");
                                }catch(ExecutionException e){
                                    System.err.println("Task encountered an exception: " + e);
                                }finally{
                                iteratorFutures.remove();
                                }
                            }
                        }
                    }
                }
                //all Users that need to authenticate, or rejoin a game or just use the menu are taken
                //care of by the following pool
                
                if(usersToDealWith.size()>0){
                        Iterator<User> iterator = usersToDealWith.iterator();
                        while (iterator.hasNext()) {
                            User task = iterator.next();
                            threadPool.execute(task);
                            iterator.remove();
                        }
                }
               
      

                synchronized(waitingQueue){
                    if(waitingQueue.size()>0){

                        Iterator<User> iterator = waitingQueue.iterator();
                        while ((this.numFullGames<maxConcurrentGames) && iterator.hasNext()) {
 
                            User user = iterator.next();
                            System.out.println("User: "+ user.getUsername() +" is gonna be taken care of");
                            joinGame(user);
                            iterator.remove();
                        }
                    }

                }
                // Increment the client count and print the updated count
                
                if(usersToDealWith.size()==0 && futures.size()==0){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    // Remove started games from queue and reset users values related to game
    public void cleanDoneGame(Game game) {
        synchronized(gameList){
            gameList.remove(game);
        }
        synchronized(numFullGames){
            numFullGames--;
        } 
        
        for(User user:game.getPlayers()){
            if(user!=null){
            user.setGameStarted(false);
            user.setGame(null); 
            } 
        }
        
    }
    public User getActiveUser(String username){
        synchronized(activeUsers){
            return activeUsers.get(username);
        }
    }
    public void removeActiveUser(String username){
        synchronized(activeUsers){
            activeUsers.remove(username);
        }
    }


    public void addToWaitingQueue(User user){
        synchronized(waitingQueue){
            waitingQueue.add(user);
        }
        synchronized(activeUsers){
            activeUsers.put(user.getUsername(), user);
        }
    }
    public void addUserToDealWith(User user){
        synchronized(usersToDealWith){
            usersToDealWith.add(user);
        }
    } 


    public synchronized void joinGame(User user) {

        while(!user.getGameStarted()){
            Game chosenGame=findAvailableGame(user.getUsername());

            synchronized(chosenGame){

                if(chosenGame.getNumPlayers()<chosenGame.getNumMaxPlayers()){
                    chosenGame.addPlayer(user);
                }
                if(chosenGame.isReadyToStart()){
                    synchronized(this.numFullGames){
                        this.numFullGames++;
                    }
                    System.out.println("Game "+ chosenGame.getId() +" ready to start");
                    user.setGameStarted(true);
                    synchronized(futures){
                    futures.add(threadPool.submit(chosenGame));
                    }
                }
                   
            }
        }

    }
    public Game findAvailableGame(String username){
        synchronized(gameList){
            if(gameList.size()>0){
                for(Game game: gameList){
                    if(game.getNumPlayers()<game.getNumMaxPlayers()){
                        System.out.println("User: "+ username +" has found a game");
                        return game;
                    }
                }
            }
            if(gameList.size()<maxConcurrentGames){
                Game newGame=new Game(this,this.numStartedGames++);
                gameList.add(newGame);
                System.out.println("User: "+ username +" needed a game to be created");
                return newGame;
                
            }
            return null;
        }
        
    }
    public boolean isPlayerConnected(User player) {
        try {
            // Try to read from the input stream with a small timeout
            PrintWriter output=player.getOutput();
            output.println("Checking connection..");
            BufferedReader inputStream = player.getInput();
            inputStream.readLine();
            return true; // No exception, socket is connected
        } catch (IOException e) {
            return false; // Exception occurred, socket is disconnected
        }
    }

}
