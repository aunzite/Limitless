package tile;

import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

public class TileManager {

    GamePanel gp; // Reference to the GamePanel instance

    Tile[] tile; // Array of Tile objects

    public TileManager (GamePanel gp){

        this.gp = gp; // Initialize the GamePanel reference
        tile = new Tile[10]; // Create an array of 10 Tile objects
        getTileImage(); // Call the method to load tile images
    }

    public void getTileImage(){

        try {
            String resPath = "res";
            // Load tile images from the resources folder
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(new File(resPath + "/tiles/grass.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(new File(resPath + "/tiles/path.png"));

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(new File(resPath + "/tiles/water.png"));

        } catch (IOException e) {
            System.out.println("Error loading tiles: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for debugging
        }
    }

    public void draw(Graphics2D g2){
        int screenCols = gp.screenWidth / gp.tileSize;
        int screenRows = gp.screenHeight / gp.tileSize;
        
        // Fill screen with grass tiles using nested loops
        for (int row = 0; row < screenRows; row++) {
            for (int col = 0; col < screenCols; col++) {
                int x = col * gp.tileSize;
                int y = row * gp.tileSize;
                g2.drawImage(tile[0].image, x, y, gp.tileSize, gp.tileSize, null);
            }
        }

        int centerPath = 7;

        //path
        g2.drawImage(tile[1].image, (centerPath-1)*48, (centerPath-2)*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath-1)*48, (centerPath-1)*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath)*48, (centerPath-3)*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath)*48, (centerPath-2)*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath)*48, (centerPath-1)*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath)*48, centerPath*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath+1)*48, (centerPath-3)*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath+1)*48, (centerPath-2)*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath+1)*48, (centerPath-1)*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath+1)*48, centerPath*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath+2)*48, (centerPath-2)*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        g2.drawImage(tile[1].image, (centerPath+2)*48, (centerPath-1)*48, gp.tileSize, gp.tileSize, null); // Draw the first tile
        //water
        //g2.drawImage(tile[2].image, 0, 0, gp.tileSize, gp.tileSize, null); // Draw the first tile

    }
}