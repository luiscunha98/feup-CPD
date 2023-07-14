import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketReception implements Runnable {
    Server server;
    ServerSocket serverSocket;
    Socket clientSocket;
    public SocketReception(Server server){
        this.server=server;
        this.serverSocket=server.getServerSocket();
    }

    public void run(){
        try {
            synchronized(serverSocket){
                clientSocket = this.serverSocket.accept();
            }
            System.out.println("New client connected");
            User newUser=new User(clientSocket, this.server, this.server.getManager());
            this.server.addUserToDealWith(newUser);
            System.out.println("User is connected: "+ newUser.getSocket().isConnected() );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
