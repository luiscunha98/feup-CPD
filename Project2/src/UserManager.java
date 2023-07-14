import java.io.*;
import java.util.*;


public class UserManager {
    private Map<String, String> users;
    private Map<String, Integer> userPoints;
    private String usersFilePath;

    public UserManager(String usersFilePath) {
        this.users = new HashMap<>();
        this.userPoints = new HashMap<>();
        this.usersFilePath = usersFilePath;
        loadUsers();
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) { // Check for three parts: username, password, points
                    String username = parts[0];
                    String password = parts[1];
                    int points = Integer.parseInt(parts[2]);
                    users.put(username, password);
                    userPoints.put(username, points);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading User: " + e.getMessage());
        }
    }
    
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String username = entry.getKey();
                String password = entry.getValue();
                int points = userPoints.getOrDefault(username, 0);
                writer.write(username + "," + password + "," + points);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving User: " + e.getMessage());
        }
    }
    
    public synchronized boolean loginUser(String username, String password) {
        String storedPassword = users.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    public synchronized boolean registerUser(String username, String password) {
        if (!users.containsKey(username)) {
            users.put(username, password);
            userPoints.put(username, 0);
            saveUsers();
            return true;
        }
        return false;
    }

    public synchronized void updatePoints(String username, int points) {
        if (userPoints.containsKey(username)) {
            int currentPoints = getUserPoints(username);
            int newPoints = Math.max(0, currentPoints + points); 
            userPoints.put(username, newPoints);
            saveUsers();
        }
    }

    public synchronized int getUserPoints(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(username)) {
                    return Integer.parseInt(parts[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading User: " + e.getMessage());
        }
        
        System.out.println("Username not found: " + username);
        return -1; // Return -1 to indicate that the username was not found
    }
    

    public synchronized String getTopPlayers(int limit) {
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(userPoints.entrySet());
        sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        StringBuilder result = new StringBuilder();
        result.append(String.format("%-15s || %-12s || %-10s\n", "   PLAYER", "   RANK", "   POINTS"));
        result.append("----------------------------------------------\n");
        for (int i = 0; i < limit && i < sortedList.size(); i++) {
            Map.Entry<String, Integer> entry = sortedList.get(i);
            String player = entry.getKey();
            int points = entry.getValue();
            String rank = getUserRank(player);
            result.append(String.format("%-15s || %-12s || %-10s\n", player, rank, points));
        }
        result.append("----------------------------------------------\n");
        return result.toString();
    }
    

    public synchronized String getUserRank(String username) {
        int points = getUserPoints(username);
        String rank;
        if (points >= 0 && points <= 10) {
            rank = "Silver";
        } else if (points >= 11 && points <= 30) {
            rank = "Gold";
        } else if (points >= 31 && points <= 60) {
            rank = "Platinum";
        } else {
            rank = "Diamond";
        }
        return rank;
    }

    public int calculatePointsNeeded(String rank, int points) {
        int pointsNeeded;
        switch (rank) {
            case "Silver":
                pointsNeeded = 11 - points;
                break;
            case "Gold":
                pointsNeeded = 31 - points;
                break;
            case "Platinum":
                pointsNeeded = 61 - points;
                break;
            default:
                pointsNeeded = 0;
                break;
        }
        return Math.max(pointsNeeded, 0); // Ensure non-negative points needed
    }
    
    
}

