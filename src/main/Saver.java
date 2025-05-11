package main;

import java.io.*;

public class Saver {
    private int playerX;
    private int playerY;

    public int getPlayerX() {
        return playerX;
    }
    public int getPlayerY() {
        return playerY;
    }
    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }
    public void setPlayerY(int playerY) {
        this.playerY = playerY;
    }
    public void saveGame(int playerX, int playerY) {
        this.playerX = playerX;
        this.playerY = playerY;
        try {
            FileWriter fw = new FileWriter("save.txt");
        } catch (IOException e) {
        }
        System.out.println("Game saved!");
    }

}
