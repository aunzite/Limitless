/////////////////////////////////////////////////////////////////////////////
// Limitless
// Player.java
// 
// Description: Main player character class. This class:
// - Handles player movement and controls (Aun)
// - Manages sprite animations and rendering (Aun)
// - Controls player position and camera centering (Aun)
// - Processes keyboard input for movement (Aun)
// - Implements sprinting mechanics (Aun)
/////////////////////////////////////////////////////////////////////////////

package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;

// Player class representing the main character in the game
// Extends the Entity class to inherit basic entity properties
public final class Player extends Entity{
    GamePanel gp;       // Reference to the GamePanel instance
    KeyHandler keyH;    // Handles keyboard input

    // Attributes (Ahmed)
    public int hp;
    public int stamina;
    public Weapon weapon;

    // Screen position constants (center of screen)
    public final int screenX; // Fixed X position on screen
    public final int screenY; // Fixed Y position on screen

    // Constructor initializes player with game panel and keyboard handler
    public Player (GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;

        hp = 100;
        stamina = 100;
        weapon = null; // Start with no weapon

        // Calculate center position of screen for player
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);
       

        playerHitbox = new Rectangle(8, 16,32, 32);

        setDefaultValues();
        getPlayerImage();
    }

    // Sets default values for player position and movement
    public void setDefaultValues (){
        // Set initial world position to center of the map
        worldX = gp.tileSize*12;
        worldY = gp.tileSize*10;
        speed = 4;              // Increased default movement speed from 3 to 4
        direction = "down";     // Default facing direction
    }
    public int getWorldX() {
        return worldX;
    }
    public int getWorldY() {
        return worldY;
    }
    public String getDirection() {
        return direction;
    }
    // Sets values for player position and movement
    public void setValues (int worldX, int worldY, String direction){

        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;     
    }

    //Extracts a single sprite from the sprite sheet
    private BufferedImage getSprite(String sheetPath, int row, int col, int spriteWidth, int spriteHeight) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));

            // Calculate position in sprite sheet with spacing and offset
            return spriteSheet.getSubimage(
                (col * spriteWidth + 9) + (col*25),  // x coordinate with offset and spacing
                (row * spriteHeight + 14) + (row*13), // y coordinate with offset and spacing
                spriteWidth,        // width of single sprite
                spriteHeight        // height of single sprite
            );
        } catch (IOException e) {
            return null;         // Return null if error occurs
        }
    }

    // Loads all player sprites from the sprite sheet
    public void getPlayerImage() {
        try {
            
            // Sprite sheet configuration
            String spriteSheetPath = "res/player/walk.png"; // Path to the sprite sheet
            int spriteWidth = 39;  // Width of each sprite frame
            int spriteHeight = 50; // Height of each sprite frame
            
            // Example: Load sprites from specific rows and columns
            // Parameters: (sheetPath, row, column, width, height)
            up1 = getSprite(spriteSheetPath, 0, 0, spriteWidth, spriteHeight);    // First row, first column
            up2 = getSprite(spriteSheetPath, 0, 1, spriteWidth, spriteHeight);    // First row, second column
            up3 = getSprite(spriteSheetPath, 0, 2, spriteWidth, spriteHeight);    // First row, third column
            up4 = getSprite(spriteSheetPath, 0, 3, spriteWidth, spriteHeight);    // First row, fourth column
            up5 = getSprite(spriteSheetPath, 0, 4, spriteWidth, spriteHeight);    // First row, fifth column
            up6 = getSprite(spriteSheetPath, 0, 5, spriteWidth, spriteHeight);    // First row, sixth column
            up7 = getSprite(spriteSheetPath, 0, 6, spriteWidth, spriteHeight);    // First row, seventh column
            up8 = getSprite(spriteSheetPath, 0, 7, spriteWidth, spriteHeight);    // First row, eighth column
            up9 = getSprite(spriteSheetPath, 0, 8, spriteWidth, spriteHeight);    // First row, ninth column
            
            left1 = getSprite(spriteSheetPath, 1, 0, spriteWidth, spriteHeight);    // Second row, first column
            left2 = getSprite(spriteSheetPath, 1, 1, spriteWidth, spriteHeight);    // Second row, second column
            left3 = getSprite(spriteSheetPath, 1, 2, spriteWidth, spriteHeight);    // Second row, third column
            left4 = getSprite(spriteSheetPath, 1, 3, spriteWidth, spriteHeight);    // Second row, fourth column
            left5 = getSprite(spriteSheetPath, 1, 4, spriteWidth, spriteHeight);    // Second row, fifth column
            left6 = getSprite(spriteSheetPath, 1, 5, spriteWidth, spriteHeight);    // Second row, sixth column
            left7 = getSprite(spriteSheetPath, 1, 6, spriteWidth, spriteHeight);    // Second row, seventh column
            left8 = getSprite(spriteSheetPath, 1, 7, spriteWidth, spriteHeight);    // Second row, eighth column
            left9 = getSprite(spriteSheetPath, 1, 8, spriteWidth, spriteHeight);    // Second row, ninth column

            down1 = getSprite(spriteSheetPath, 2, 0, spriteWidth, spriteHeight);    // Third row, first column
            down2 = getSprite(spriteSheetPath, 2, 1, spriteWidth, spriteHeight);    // Third row, second column
            down3 = getSprite(spriteSheetPath, 2, 2, spriteWidth, spriteHeight);    // Third row, third column
            down4 = getSprite(spriteSheetPath, 2, 3, spriteWidth, spriteHeight);    // Third row, fourth column
            down5 = getSprite(spriteSheetPath, 2, 4, spriteWidth, spriteHeight);    // Third row, fifth column
            down6 = getSprite(spriteSheetPath, 2, 5, spriteWidth, spriteHeight);    // Third row, sixth column
            down7 = getSprite(spriteSheetPath, 2, 6, spriteWidth, spriteHeight);    // Third row, seventh column
            down8 = getSprite(spriteSheetPath, 2, 7, spriteWidth, spriteHeight);    // Third row, eighth column
            down9 = getSprite(spriteSheetPath, 2, 8, spriteWidth, spriteHeight);    // Third row, ninth column
            
            right1 = getSprite(spriteSheetPath, 3, 0, spriteWidth, spriteHeight);    // Fourth row, first column
            right2 = getSprite(spriteSheetPath, 3, 1, spriteWidth, spriteHeight);    // Fourth row, second column
            right3 = getSprite(spriteSheetPath, 3, 2, spriteWidth, spriteHeight);    // Fourth row, third column
            right4 = getSprite(spriteSheetPath, 3, 3, spriteWidth, spriteHeight);    // Fourth row, fourth column
            right5 = getSprite(spriteSheetPath, 3, 4, spriteWidth, spriteHeight);    // Fourth row, fifth column
            right6 = getSprite(spriteSheetPath, 3, 5, spriteWidth, spriteHeight);    // Fourth row, sixth column
            right7 = getSprite(spriteSheetPath, 3, 6, spriteWidth, spriteHeight);    // Fourth row, seventh column
            right8 = getSprite(spriteSheetPath, 3, 7, spriteWidth, spriteHeight);    // Fourth row, eighth column
            right9 = getSprite(spriteSheetPath, 3, 8, spriteWidth, spriteHeight);    // Fourth row, ninth column
            
        } catch (Exception e) {}
    }
    
    // Updates player position and animation state based on input
    public void update(){
        // Don't process movement if in dialogue
        if (gp.gameState == GamePanel.DIALOGUE_STATE) {
            return;
        }

        boolean isMoving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;
        
        // Only update if movement keys are pressed
        if(isMoving) {
            // Update direction and position based on key input
            if(keyH.upPressed) {
                direction = "up";
            }
            if(keyH.downPressed) {
                direction = "down";
            }
            if(keyH.leftPressed) {
                direction = "left";
            }
            if(keyH.rightPressed) {
                direction = "right";
            }

            collisionOn = false; // Reset collision state
            
            // Check tile collision
            gp.cCheck.checkTile(this);

            if (!collisionOn) {
                // Handle sprinting
                if (keyH.shiftPressed && gp.hud.canSprint()) {
                    speed = 8;  // Sprint speed
                    gp.hud.drainStamina();
                } else {
                    speed = 4;   // Normal speed
                }
                
                // Move the player
                switch(direction) {
                    case "up" -> worldY -= speed;
                    case "down" -> worldY += speed;
                    case "left" -> worldX -= speed;
                    case "right" -> worldX += speed;
                }
                
                // Only update animation if actually moving
                spriteCounter++;
                if (spriteCounter > 12) {
                    if (spriteNum == 1) {
                        spriteNum = 2;
                    } else if (spriteNum == 2) {
                        spriteNum = 3;
                    } else if (spriteNum == 3) {
                        spriteNum = 4;
                    } else {
                        spriteNum = 1;
                    }
                    spriteCounter = 0;
                }
            }
        } else {
            // Reset animation when not moving
            spriteNum = 1;
            spriteCounter = 0;
        }
        
        // Update stamina
        gp.hud.regenerateStamina(isMoving);
        stamina = gp.hud.getStamina();
        
        // Update HUD
        String weaponName = weapon != null ? weapon.getName() : "No Weapon";
        gp.hud.update(hp, stamina, weaponName, isMoving);
    }
    
    // Draws the player with current sprite based on direction and animation frame
    public void draw(Graphics2D g2){
        BufferedImage image = null;
        
        // Select correct sprite based on direction and animation frame
        switch(direction){
            case "up":
                switch (spriteNum) {
                    case 1:
                        image = up1;
                        break;
                    case 2:
                        image = up2;
                        break;
                    case 3:
                        image = up3;
                        break;
                    case 4:
                        image = up4;
                        break;
                    case 5:
                        image = up5;
                        break;
                    case 6:
                        image = up6;
                        break;
                    case 7:
                        image = up7;
                        break;
                    case 8:
                        image = up8;
                        break;
                    case 9:
                        image = up9;
                        break;
                }
                break;

            case "down":
                switch (spriteNum) {
                    case 1:
                        image = down1;
                        break;
                    case 2:
                        image = down2;
                        break;
                    case 3:
                        image = down3;
                        break;
                    case 4:
                        image = down4;
                        break;
                    case 5:
                        image = down5;
                        break;
                    case 6:
                        image = down6;
                        break;
                    case 7:
                        image = down7;
                        break;
                    case 8:
                        image = down8;
                        break;
                    case 9:
                        image = down9;
                        break;
                }
                break;

            case "left":
                switch (spriteNum) {
                    case 1:
                        image = left1;
                        break;
                    case 2:
                        image = left2;
                        break;
                    case 3:
                        image = left3;
                        break;
                    case 4:
                        image = left4;
                        break;
                    case 5:
                        image = left5;
                        break;
                    case 6:
                        image = left6;
                        break;
                    case 7:
                        image = left7;
                        break;
                    case 8:
                        image = left8;
                        break;
                    case 9:
                        image = left9;
                        break;
                }
                break;

            case "right":
                switch (spriteNum) {
                    case 1:
                        image = right1;
                        break;
                    case 2:
                        image = right2;
                        break;
                    case 3:
                        image = right3;
                        break;
                    case 4:
                        image = right4;
                        break;
                    case 5:
                        image = right5;
                        break;
                    case 6:
                        image = right6;
                        break;
                    case 7:
                        image = right7;
                        break;
                    case 8:
                        image = right8;
                        break;
                    case 9:
                        image = right9;
                        break;
                }
                break;
        }

        
        
        // Draw the player sprite at screen position
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
