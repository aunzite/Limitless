/////////////////////////////////////////////////////////////////////////////
// Limitless
// Entity.java
// 
// Description: Base class for all game entities. This class:
// - Defines core entity properties and states (Aun)
// - Manages entity positioning and movement (Aun)
// - Handles sprite animation framework (Aun)
// - Provides common entity functionality (Aun)
// - Controls directional state management (Aun)
/////////////////////////////////////////////////////////////////////////////

package entity;

import java.awt.image.BufferedImage;

// Base class for all game entities (players, NPCs, etc.)

public class Entity {
    // World position coordinates
    public int worldX, worldY;
    
    // Movement speed of the entity
    public int speed;

    // Sprite images for different directions of movement
    // Each direction has 9 animation frames
    public BufferedImage up1, up2, up3, up4, up5, up6, up7, up8, up9,       // Upward movement sprites
                         down1, down2, down3, down4, down5, down6, down7, down8, down9,   // Downward movement sprites
                         left1, left2, left3, left4, left5, left6, left7, left8, left9,   // Left movement sprites
                         right1, right2, right3, right4, right5, right6, right7, right8, right9; // Right movement sprites

    // Current facing direction of the entity
    public String direction;

    // Animation variables
    public int spriteCounter = 0;    // Controls animation timing
    public int spriteNum = 1;        // Current sprite frame number (1-9)
}