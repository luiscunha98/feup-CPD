import java.io.*;
import java.net.*;
public class User implements Runnable {
    private Socket socket;
    private Server server;
    private PrintWriter output;
    private BufferedReader input;
    private String username;
    private boolean lookingForGame;
    private boolean gameStarted;
    private boolean authenticated;
    private UserManager userManager;
   
    private Game currentGame;
    private int indexInGame;

    public User(Socket socket, Server server, UserManager userManager) throws IOException {
        this.socket = socket;
        this.server = server;
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.lookingForGame=false;
        this.authenticated = false;
        this.gameStarted=false;
        this.userManager = userManager;
        this.currentGame=null;

    }

    public void setIndexInGame(int index){
        this.indexInGame=index;
    }
    public int getIndexInGame(){
        return this.indexInGame;
    }

    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket){
        this.socket=socket;
    }
    
    public PrintWriter getOutput() {
        return output;
    }
    public void setOutput(PrintWriter output){
        this.output=output;
    }

    public BufferedReader getInput() {
        return input;
    }
    public void setInput(BufferedReader input){
        this.input=input;
    }
    public Game getGame(){
        return currentGame;
    }

    public void setGame(Game currentGame){
        this.currentGame=currentGame;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void sendMessage(String message) {
        output.println(message);
    }
    public void setLookingForGame(boolean lookingForGame){
        this.lookingForGame=lookingForGame;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
    public boolean getGameStarted(){
        return this.gameStarted;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Socket closed.");
        }
    }

    public boolean login(String username, String password) {
        if (userManager.loginUser(username, password)) {
            this.authenticated = true;
            setUsername(username);
            return true;
        } else {
            return false;
        }
    }

    public boolean register(String username, String password) {
        if (userManager.registerUser(username, password)) {
            this.authenticated = true;
            setUsername(username);
            return true;
        } else {
            return false;
        }
    }

    private void authenticate() throws IOException {

        while (!authenticated) {
            // Authenticate or register the user
            output.println("+---------------------------+");
            output.println("|   Login or Register?      |");
            output.println("+---------------------------+");
            output.println("|   1. Login                |");
            output.println("|   2. Register             |");
            output.println("+---------------------------+");
            output.println("Enter your choice (1/2): ");
            int choice = Integer.parseInt(input.readLine());

            if (choice == 1) {
                output.println("+---------------------------+");
                output.println("|          LOGIN            |");
                output.println("+---------------------------+");
                output.println("Username: ");
                String username = input.readLine();
                output.println("Password: ");
                String password = input.readLine();

                if (!login(username, password)) {
                    output.println("Invalid username or password.");
                }
            } else if (choice == 2) {
                output.println("+---------------------------+");
                output.println("|         REGISTER          |");
                output.println("+---------------------------+");
                output.println("Please enter a new username: ");
                String username = input.readLine();
                output.println("Please enter a password: ");
                String password = input.readLine();

                if (!register(username, password)) {
                    output.println("Failed to register. The username may already exist.");
                }
            }
        }
        output.println("You have been successfully authenticated.");
        output.println("");
        output.println("");
    }

    public void menuAgent() throws IOException, InterruptedException {
        
        Menu menu = new Menu(input, output);
        String choice;
        boolean exit = false;
        while (!exit) {

            menu.display();
            
            choice = menu.getChoice();

            switch (choice) {
                case "1": 
                    output.println("");
                    output.println("Looking for game...");
                    lookingForGame = true;
                    exit=true;
                    System.out.println("Case 1 is ending");
                    break;

                case "2":
                    // Display user's rank
                    String rank = userManager.getUserRank(username);
                    int userPoints = userManager.getUserPoints(username);
                    int pointsNeeded = userManager.calculatePointsNeeded(rank, userPoints);
                    String userIDText = " ID: " + username;
                    String rankText = " Rank: " + rank;
                    String pointsText = " (" + userPoints + " Points )";
                    String pointsNeededText = "Points needed for the next rank: " + pointsNeeded;
                
                    int maxLength = Math.max(Math.max(userIDText.length(), rankText.length()), pointsNeededText.length());
                    int boxWidth = maxLength + 14;
                
                    output.println("+" + "-".repeat(boxWidth) + "+");
                    output.println("|" + " ".repeat((boxWidth - 4) / 2) + " RANK" + " ".repeat((boxWidth - 4) / 2) + "|");
                    output.println("+" + "-".repeat(boxWidth) + "+");
                    output.println("|" + " ".repeat((boxWidth - userIDText.length()) / 2) + userIDText + " ".repeat((boxWidth - userIDText.length()) / 2) + "|");
                    output.println("+" + "-".repeat(boxWidth) + "+");
                    output.println("|" + " ".repeat((boxWidth - rankText.length()) / 2) + rankText + " ".repeat((boxWidth - rankText.length()) / 2) + "|");
                    output.println("|" + " ".repeat((boxWidth - pointsText.length()) / 2) + pointsText + " ".repeat((boxWidth - pointsText.length()) / 2) + "|");
                    output.println("+" + "-".repeat(boxWidth) + "+");
                    output.println("|" + " ".repeat((boxWidth - pointsNeededText.length()) / 2) + pointsNeededText + " ".repeat((boxWidth - pointsNeededText.length()) / 2) + "|");
                    output.println("+" + "-".repeat(boxWidth) + "+");
                    output.println("");
                    
                    break;
                
                case "3":
                    // Display the ranking
                    String ranking = userManager.getTopPlayers(10);
                    String[] rankingLines = ranking.split("\n");

                    int maxLength1 = 0;
                    for (String line : rankingLines) {
                        maxLength1 = Math.max(maxLength1, line.length());
                    }

                    int boxWidth1 = maxLength1 + 4;

                    output.println("+" + "-".repeat(boxWidth1) + "+");
                    output.println("|" + " ".repeat((boxWidth1 - 6) / 2) + "RANKING" + " ".repeat(21) + "|");
                    output.println("+" + "-".repeat(boxWidth1) + "+");



                    for (String line : rankingLines) {
                        int spacesToAdd = boxWidth1 - line.length();
                        int leftSpaces = spacesToAdd / 2;
                        int rightSpaces = spacesToAdd - leftSpaces;
                        output.println("|" + " ".repeat(leftSpaces) + line + " ".repeat(rightSpaces) + "|");
                    }

                    output.println("+" + "-".repeat(boxWidth1) + "+");
                    output.println("");

                    break;
                
                case "4" :
                    String message = "Thank you for playing";
                    int messageLength = message.length();

                    int boxWidth2 = messageLength + 20;

                    output.println("+" + "-".repeat(boxWidth2) + "+");
                    output.println("|" + " ".repeat((boxWidth2 - messageLength) / 2) + message + " ".repeat((boxWidth2 - messageLength) / 2) + "|");
                    output.println("+" + "-".repeat(boxWidth2) + "+");

                    output.println("Exiting...");
                    // Exit
                    exit = true;
                    
                    break;

                default: 
                    output.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    @Override
    public void run() {
        output.println("Welcome!");

        try {
            User activeUser;
            Game gameToJoin;
            //if not authenticated yet
            if(!this.authenticated){
                authenticate();
            }

            //check here if it is already in a game
            if((activeUser= this.server.getActiveUser(username))!=null){//the user is either in waitingqueue or game
                activeUser.setSocket(this.socket);
                activeUser.setInput(this.input);
                activeUser.setOutput(this.output);
                if(activeUser.getGameStarted()){//if the user has already been added to a game
                    gameToJoin=activeUser.getGame();
                    gameToJoin.setInput(this.input, activeUser.getIndexInGame());
                    gameToJoin.setOutput(this.output, activeUser.getIndexInGame());
                }
            }
            else{
                menuAgent();
                if(lookingForGame){
                    this.server.addToWaitingQueue(this);
                    System.out.println("User: "+ username +" added to waiting queue");
                }
                else{
                    this.socket.close();
                    System.out.println("User: "+ username +" has disconnected");
                }
            }
            
        }catch (IOException | InterruptedException e){
            System.out.println("User disconnected because of an error");
        }
    }

    public void earnPoints(GAMERESULT gameresult) {
        switch (gameresult) {
            case FIRST:
             userManager.updatePoints(username, 3);
             break;
            case SECOND:
             userManager.updatePoints(username, 1);
             break;
            case THIRD:
             userManager.updatePoints(username, -1);
             break;
            case FORTH:userManager.updatePoints(username, -3);
        }
    }
}
