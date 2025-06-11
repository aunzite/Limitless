/////////////////////////////////////////////////////////////////////////////
// Limitless
// AssetSetter.java
// 
// Description: Manages game asset placement including:
// - Object placement
// - NPC placement
// - Item placement
// - World decoration
/////////////////////////////////////////////////////////////////////////////

package main;
import object.OBJ_Apple;
import object.OBJ_Solthorn;

// Manages game asset placement and initialization
public class AssetSetter {
    // Game panel reference
    private GamePanel gp;
    
    // Asset placement settings
    private static final int TILE_SIZE = 48;        // Size of each tile
    private static final int WORLD_WIDTH = 50;      // World width in tiles
    private static final int WORLD_HEIGHT = 50;     // World height in tiles
    
    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }
    
    private boolean isCollisionTile(int worldX, int worldY) {
        int col = worldX / gp.tileSize;
        int row = worldY / gp.tileSize;
        
        // Check if coordinates are within map bounds
        if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
            return true;
        }
        
        // Get tile number at this position
        int tileNum = gp.tileM.mapTileNum[col][row];
        
        // Check if the tile has collision
        return gp.tileM.tile[tileNum].collision;
    }
    
    public void setObject() {
        // Place apples
        gp.obj[0] = new OBJ_Apple(1);
        gp.obj[0].worldX = 23 * gp.tileSize;
        gp.obj[0].worldY = 7 * gp.tileSize;

        // Place Solthorn
        gp.obj[1] = new OBJ_Solthorn();
        gp.obj[1].worldX = 23 * gp.tileSize;
        gp.obj[1].worldY = 40 * gp.tileSize;
    }
}
