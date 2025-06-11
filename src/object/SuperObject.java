/////////////////////////////////////////////////////////////////////////////
// Limitless
// SuperObject.java
// 
// Description: Base class for all game objects including:
// - Item objects
// - Interactive objects
// - Decorative objects
// - Collectible objects
/////////////////////////////////////////////////////////////////////////////

package object;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import main.GamePanel;

// Base class for all game objects
public class SuperObject {
    // Object properties
    public BufferedImage image;      // Object's sprite
    public String name;              // Object's name
    public boolean collision = false; // Collision flag
    public int worldX, worldY;       // World position
    public Rectangle hitbox;         // Collision hitbox
    public int hitboxDefaultX = 0;   // Default hitbox X offset
    public int hitboxDefaultY = 0;   // Default hitbox Y offset
    public GamePanel gp;             // Game panel reference

    // Draw the object on screen
    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Only draw if object is on screen
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}   
