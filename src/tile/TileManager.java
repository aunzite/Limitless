/////////////////////////////////////////////////////////////////////////////
// Limitless
// TileManager.java
// 
// Description: Manages game tiles including:
// - Tile loading and storage
// - Map rendering
// - Tile type management
// - World map handling
/////////////////////////////////////////////////////////////////////////////

package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import main.GamePanel;

// Manages all tile-related functionality
public class TileManager {
    // Game panel reference
    private GamePanel gp;
    
    // Tile arrays
    public Tile[] tile;             // Available tile types
    public int[][] mapTileNum;      // World map tile layout
    
    // Map settings
    private static final int MAX_TILES = 50;     // Maximum number of tile types
    private static final int MAP_WIDTH = 50;     // Map width in tiles
    private static final int MAP_HEIGHT = 50;    // Map height in tiles
    
    // Tile dimensions
    private static final int TILE_WIDTH = 48;    // Tile width in pixels
    private static final int TILE_HEIGHT = 48;   // Tile height in pixels

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[MAX_TILES];
        mapTileNum = new int[MAP_WIDTH][MAP_HEIGHT];
        getTileImage();
        loadMap("/maps/world01.txt");
    }

    // Load tile images and set their properties
    public void getTileImage() {
        // Load and initialize tile types
        // TODO: Implement tile loading
    }

    // Load map data from file
    public void loadMap(String mapPath) {
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            int col = 0;
            int row = 0;
            
            while (col < MAP_WIDTH && row < MAP_HEIGHT) {
                String line = br.readLine();
                
                while (col < MAP_WIDTH) {
                    String[] numbers = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if (col == MAP_WIDTH) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Draw the world map
    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;
        
        while (worldCol < MAP_WIDTH && worldRow < MAP_HEIGHT) {
            int tileNum = mapTileNum[worldCol][worldRow];
            
            int worldX = worldCol * TILE_WIDTH;
            int worldY = worldRow * TILE_HEIGHT;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;
            
            // Only draw tiles that are on screen
            if (worldX + TILE_WIDTH > gp.player.worldX - gp.player.screenX &&
                worldX - TILE_WIDTH < gp.player.worldX + gp.player.screenX &&
                worldY + TILE_HEIGHT > gp.player.worldY - gp.player.screenY &&
                worldY - TILE_HEIGHT < gp.player.worldY + gp.player.screenY) {
                
                g2.drawImage(tile[tileNum].image, screenX, screenY, TILE_WIDTH, TILE_HEIGHT, null);
            }
            
            worldCol++;
            
            if (worldCol == MAP_WIDTH) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}