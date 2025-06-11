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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

// Base class for all game entities (players, NPCs, etc.)

public class Entity {
    // World position coordinates
    public int worldX;
    public int worldY;
    
    // Movement speed of the entity
    public int speed;

    // Sprite images for different directions of movement
    // Each direction has 9 animation frames
    public BufferedImage up1;
    public BufferedImage up2;
    public BufferedImage up3;
    public BufferedImage up4;
    public BufferedImage up5;
    public BufferedImage up6;
    public BufferedImage up7;
    public BufferedImage up8;
    public BufferedImage up9;

    public BufferedImage down1;
    public BufferedImage down2;
    public BufferedImage down3;
    public BufferedImage down4;
    public BufferedImage down5;
    public BufferedImage down6;
    public BufferedImage down7;
    public BufferedImage down8;
    public BufferedImage down9;

    public BufferedImage left1;
    public BufferedImage left2;
    public BufferedImage left3;
    public BufferedImage left4;
    public BufferedImage left5;
    public BufferedImage left6;
    public BufferedImage left7;
    public BufferedImage left8;
    public BufferedImage left9;

    public BufferedImage right1;
    public BufferedImage right2;
    public BufferedImage right3;
    public BufferedImage right4;
    public BufferedImage right5;
    public BufferedImage right6;
    public BufferedImage right7;
    public BufferedImage right8;
    public BufferedImage right9;

    // Current facing direction of the entity
    public String direction;

    // Animation variables
    public int spriteCounter;
    public int spriteNum;

    public Rectangle playerHitbox;
    public boolean collisionOn;
}