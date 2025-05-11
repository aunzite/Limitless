package main;
import java.io.*;
import entity.Player;

public class Saver {
    GamePanel gp;
    KeyHandler keyH;
    private int playerX;
    private int playerY;
    private String direction;

    public Player player = new Player(this.gp, this.keyH);

    public Saver() {
        this.playerX = gp.tileSize*12;
        this.playerY = gp.tileSize*10;
        this.direction = "down";
    }
    public int getPlayerX() {
        return playerX;
    }
    public int getPlayerY() {
        return playerY;
    }
    public String getDirection() {
        return direction;
    }
    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }
    public void setPlayerY(int playerY) {
        this.playerY = playerY;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
    public void saveGame(int playerX, int playerY, String direction) {
        setPlayerX(playerX);
        setPlayerY(playerY);
        setDirection(direction);

        // Add more data to save here

        try {
            FileWriter fw = new FileWriter("save.txt");
            PrintWriter pw = new PrintWriter(fw);

            pw.println("playerX\n"+getPlayerX());
            pw.println("playerY\n"+getPlayerY());
            pw.println("direction\n"+getDirection());

            //add more data to save here

            pw.close();
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving game...");
        }
        System.out.println("Game saved!");
    }

    public void deleteSave() {
        saveGame(gp.tileSize*12, gp.tileSize*10, "down");
        System.out.println("Save data deleted.");
    }
    public void loadGame() {
        try {
            FileReader fr = new FileReader("save.txt");
            BufferedReader br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("playerX")) {
                    setPlayerX(Integer.parseInt(br.readLine()));
                } else if (line.equals("playerY")) {
                    setPlayerY(Integer.parseInt(br.readLine()));
                } else if (line.equals("direction")) {
                    setDirection(br.readLine());
                }
                // Add more data to load here
            }
            player.setValues(getPlayerX(), getPlayerY(), getDirection());
            System.out.println("Game loaded!");

            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Error loading game...");
        }
    }

}
