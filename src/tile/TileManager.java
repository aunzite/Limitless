package tile;

import java.awt.Graphics2D;
import java.io.*;
import javax.imageio.ImageIO;
import main.GamePanel;

public class TileManager {

    GamePanel gp; // Reference to the GamePanel instance
    Tile[] tile; // Array of Tile objects
    int mapTileNum[][];

    public TileManager (GamePanel gp){

        this.gp = gp; // Initialize the GamePanel reference
        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap("res/maps/world01.txt"); 
    }

    public void getTileImage(){

        try {
            //grass
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(new File("res/tiles/grass.png"));

            //path
            tile[1] = new Tile();
            tile[1].image = ImageIO.read(new File("res/tiles/path.png"));

            //water
            tile[2] = new Tile();
            tile[2].image = ImageIO.read(new File("res/tiles/water.png"));

        } catch (IOException e) {
            e.printStackTrace(); // Print the stack trace for debugging
        }
    }
    public void loadMap(String filePath) {
        try {
            // Use File instead of getResourceAsStream for direct file access
            File mapFile = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(mapFile));
    
            int col = 0;
            int row = 0;
    
            // Read the map file line by line
            while (row < gp.maxWorldRow) {
                String line = br.readLine();

                String[] numbers = line.split(" ");

                // Check if the line is null or empty
                for (col = 0; col < gp.maxWorldCol; col++) {

                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;

                }
                row++;
            }
            br.close();
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2){

        // Variables
        int worldCol = 0;
        int worldRow = 0;

        
        // Loop through the mapTileNum array to draw the tiles on the screen
        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

            int tileNum = mapTileNum[worldCol][worldRow]; // Get the tile number from the mapTileNum array

            int worldX = worldCol * gp.tileSize; // Calculate the world X position
            int worldY = worldRow * gp.tileSize; // Calculate the world Y position
            int screenX = worldX - gp.player.worldX + gp.player.screenX; // Calculate the screen X position
            int screenY = worldY - gp.player.worldY + gp.player.screenY; // Calculate the screen Y position

            g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            worldCol++;
 
            // Check if the column exceeds the maximum screen column
            // If it does, reset the column to 0 and move to the next row
            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}