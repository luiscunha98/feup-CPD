import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader input = null;
        PrintWriter output = null;
        try {
            String hostname = args[0];
            int port = Integer.parseInt(args[1]);
            socket = new Socket(hostname, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // Create separate threads for input and output operations
            Thread inputThread = new Thread(new InputRunnable(input,socket,output));
            Thread outputThread = new Thread(new OutputRunnable(output,socket));

            // Start the threads
            inputThread.start();
            outputThread.start();

            // Wait for the threads to finish
            inputThread.join();
            outputThread.interrupt();
            outputThread.join();

           
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally{
            try {
                // Close the streams and socket
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class InputRunnable implements Runnable {
        private BufferedReader input;
        private Socket socket;
        private PrintWriter output;

        public InputRunnable(BufferedReader input,Socket socket,PrintWriter output) {
            this.socket=socket;
            this.input = input;
            this.output=output;
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = input.readLine()) != null) {
                    // Process input from the server
                    System.out.println(line);
                    if(line.equals("Checking connection..")){
                        output.println(line);
                    }
                }
                System.out.println("Press enter to close");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class OutputRunnable implements Runnable {
        private PrintWriter output;
        private BufferedReader consoleInput;
        private Socket socket;

        public OutputRunnable(PrintWriter output,Socket socket) {
            this.output = output;
            this.socket=socket;
            this.consoleInput = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public void run() {
            try {
                String line;
                while (!Thread.currentThread().isInterrupted()){
                    
                        if(((line = consoleInput.readLine()) != null))
                            output.println(line);// Send user input to the server
                            

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
