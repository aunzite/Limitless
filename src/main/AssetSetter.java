package main;
import object.OBJ_Apple;
import object.OBJ_Key;

public class AssetSetter {
    GamePanel gp;
    
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
        // Place keys
        gp.obj[0] = new OBJ_Key();
        gp.obj[0].worldX = 7 * gp.tileSize;
        gp.obj[0].worldY = 14 * gp.tileSize;

        gp.obj[1] = new OBJ_Key();
        gp.obj[1].worldX = 9 * gp.tileSize;
        gp.obj[1].worldY = 14 * gp.tileSize;
        
        // Place apples in different areas, avoiding collision tiles
        // Forest area
        if (!isCollisionTile(15 * gp.tileSize, 20 * gp.tileSize)) {
            gp.obj[2] = new OBJ_Apple();
            gp.obj[2].worldX = 15 * gp.tileSize;
            gp.obj[2].worldY = 20 * gp.tileSize;
        }
        
        if (!isCollisionTile(17 * gp.tileSize, 22 * gp.tileSize)) {
            gp.obj[3] = new OBJ_Apple();
            gp.obj[3].worldX = 17 * gp.tileSize;
            gp.obj[3].worldY = 22 * gp.tileSize;
        }
        
        // Near water
        if (!isCollisionTile(25 * gp.tileSize, 15 * gp.tileSize)) {
            gp.obj[4] = new OBJ_Apple();
            gp.obj[4].worldX = 25 * gp.tileSize;
            gp.obj[4].worldY = 15 * gp.tileSize;
        }
        
        // Near NPC
        if (!isCollisionTile(30 * gp.tileSize, 10 * gp.tileSize)) {
            gp.obj[5] = new OBJ_Apple();
            gp.obj[5].worldX = 30 * gp.tileSize;
            gp.obj[5].worldY = 10 * gp.tileSize;
        }
        
        // Near starting area
        if (!isCollisionTile(10 * gp.tileSize, 8 * gp.tileSize)) {
            gp.obj[6] = new OBJ_Apple();
            gp.obj[6].worldX = 10 * gp.tileSize;
            gp.obj[6].worldY = 8 * gp.tileSize;
        }
    }
}
