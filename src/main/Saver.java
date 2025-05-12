/////////////////////////////////////////////////////////////////////////////
// Limitless
// Saver.java
// 
// Description: Manages game save/load functionality. This class:
// - Handles saving game state to file (Aun)
// - Manages loading saved game data (Aun)
// - Controls player position persistence (Aun)
// - Processes save/load/delete commands (Aun)
// - Implements file I/O operations (Aun)
/////////////////////////////////////////////////////////////////////////////

package main;
import java.io.*;

public class Saver {
    // Reference to main game panel and player state variables
    private GamePanel gp;
    private int playerX;        // Player's X coordinate in world
    private int playerY;        // Player's Y coordinate in world
    private String direction;   // Player's facing direction

    // Constructor: Sets up initial player position and direction
    public Saver(GamePanel gp) {
        this.gp = gp;
        this.playerX = gp.tileSize*12;    // Default spawn X
        this.playerY = gp.tileSize*10;    // Default spawn Y
        this.direction = "down";          // Default direction
    }

    // Getter methods for player state
    public int getPlayerX() { 
        return playerX; 
    }
    public int getPlayerY() { 
        return playerY; 
    }
    public String getDirection() { 
        return direction; 
    }

    // Setter methods for player state
    public void setPlayerX(int playerX) { 
        this.playerX = playerX; 
    }
    public void setPlayerY(int playerY) { 
        this.playerY = playerY; 
    }
    public void setDirection(String direction) { 
        this.direction = direction; 
    }

    // Saves current game state to file
    public void saveGame(int playerX, int playerY, String direction) {
        // Update local state variables
        setPlayerX(playerX);
        setPlayerY(playerY);
        setDirection(direction);

        // Add more data to save here

        try {
            // Set up file writers
            FileWriter fw = new FileWriter("save.txt");
            PrintWriter pw = new PrintWriter(fw);

            // Write player position and direction
            pw.println("playerX\n" + getPlayerX());
            pw.println("playerY\n" + getPlayerY());
            pw.println("direction\n" + getDirection());

            //add more data to save here

            // Cleanup and notify
            System.out.println("Game saved!");
            pw.close();
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving game...");
        }
    }

    // Resets save file to default values
    public void deleteSave() {
        saveGame(gp.tileSize*12, gp.tileSize*10, "down");
        System.out.println("Save data deleted.");
    }

    // Loads game state from save file
    public void loadGame() {
        try {
            // Set up file readers
            FileReader fr = new FileReader("save.txt");
            BufferedReader br = new BufferedReader(fr);

            // Read file line by line
            String line;
            while ((line = br.readLine()) != null) {
                // Process each type of saved data
                switch (line) {
                    case "playerX":
                        setPlayerX(Integer.parseInt(br.readLine()));
                        break;
                    case "playerY":
                        setPlayerY(Integer.parseInt(br.readLine()));
                        break;
                    case "direction":
                        setDirection(br.readLine());
                        break;
                }
            }
            // Update player with loaded values
            gp.player.setValues(getPlayerX(), getPlayerY(), getDirection());
            System.out.println("Game loaded!");

            // Cleanup
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Error loading game...");
        }
    }

    // Handles keyboard input for save/load/delete operations
    public void handleInput(boolean savePressed, boolean loadPressed, boolean deletePressed) {
        if (savePressed) {
            saveGame(gp.player.worldX, gp.player.worldY, gp.player.direction);        
        }
        if (loadPressed) {
            loadGame();
        }
        if (deletePressed) {
            deleteSave();
        }
    }
}