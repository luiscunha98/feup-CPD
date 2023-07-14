import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Menu {
    private BufferedReader input;
    private PrintWriter output;

    public Menu(BufferedReader input, PrintWriter output) {
        this.input = input;
        this.output = output;
    }

    public void display() {
        output.println("+-----------------------------------------------------------+");
        output.println("|                     Welcome to Type Racing                |");
        output.println("+-----------------------------------------------------------+");
        output.println("|                           Menu                            |");
        output.println("+-----------------------------------------------------------+");
        output.println("|       1. Start Game                                       |");
        output.println("|       2. My Rank                                          |");
        output.println("|       3. View Ranking                                     |");
        output.println("|                                                           |");
        output.println("|       4. Exit                                             |");
        output.println("+-----------------------------------------------------------+");
        output.println("Enter your choice: ");
    }

    public String getChoice() throws IOException {
        return input.readLine();
    }
}

