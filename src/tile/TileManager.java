package tile;

import java.awt.Graphics2D;
import java.io.*;
import javax.imageio.ImageIO;
import main.GamePanel;

// Manages the game's tile system, including loading and rendering tiles
public final class TileManager {
    GamePanel gp;           // Reference to the main game panel
    Tile[] tile;           // Array to store different types of tiles
    int mapTileNum[][];    // 2D array storing the map layout

    // Constructor initializes tile system and loads resources
    public TileManager (GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];    // Support up to 10 different tile types
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];    // Create map array

        getTileImage();          // Load tile images
        loadMap("res/maps/world01.txt");    // Load map data
    }

    // Loads all tile images from files
    public void getTileImage() {
        try {
            // Load different tile types
            // Index 0: Grass (background)
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(new File("res/tiles/grass.png"));

            // Index 1: Path
            tile[1] = new Tile();
            tile[1].image = ImageIO.read(new File("res/tiles/path.png"));

            // Index 2: Water
            tile[2] = new Tile();
            tile[2].image = ImageIO.read(new File("res/tiles/water.png"));

        } catch (IOException e) {}
    }

    // Loads map data from a text file
    @SuppressWarnings("ConvertToTryWithResources")
    public void loadMap(String filePath) {
        try {
            // Read map file
            File mapFile = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(mapFile));
    
            int col;
            int row = 0;
    
            // Process map file line by line
            while (row < gp.maxWorldRow) {
                String line = br.readLine();                  // Read one line
                String[] numbers = line.split(" ");     // Split into numbers

                // Convert each number to a tile index
                for (col = 0; col < gp.maxWorldCol; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;     // Store in map array
                }
                row++;
            }
            br.close();
    
        } catch (IOException | NumberFormatException e) {}
    }

    // Renders the visible tiles on screen
    public void draw(Graphics2D g2) {

        // Current position in the world map
        int worldCol = 0;
        int worldRow = 0;
        
        // Loop through entire map
        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            
            // Get current tile type
            int tileNum = mapTileNum[worldCol][worldRow];

            // Calculate positions
            int worldX = worldCol * gp.tileSize;                            // Position in world
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;    // Position on screen
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            // Only draw tiles visible within camera bounds
            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX && 
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY && 
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
            
            worldCol++;
 
            // Move to next row when reaching end of column
            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}