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

    // Animation state
    private String animationState = "idle"; // Can be "idle", "walk", or "run"

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
    private BufferedImage getSprite(String sheetPath, int row, int col, int spriteWidth, int spriteHeight, int offsetX, int offsetY) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));
            if (spriteSheet == null) {
                System.err.println("Failed to load sprite sheet: " + sheetPath);
                return null;
            }

            int colGap = 30;
            int rowGap = 10;
            // Calculate position in sprite sheet with gaps
            int x = offsetX + col * (spriteWidth + colGap);
            int y = offsetY + row * (spriteHeight + rowGap);

            // Ensure we don't go out of bounds
            if (x + spriteWidth > spriteSheet.getWidth() || y + spriteHeight > spriteSheet.getHeight()) {
                System.err.println("Sprite position out of bounds: " + sheetPath + " at row " + row + ", col " + col);
                return null;
            }

            return spriteSheet.getSubimage(x, y, spriteWidth, spriteHeight);
        } catch (IOException e) {
            System.err.println("Error loading sprite: " + e.getMessage());
            return null;
        }
    }

    // Helper for idle sprites using exact pixel offsets
    private BufferedImage getIdleSprite(String sheetPath, int x, int y, int width, int height) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));
            if (spriteSheet == null) return null;
            if (x + width > spriteSheet.getWidth() || y + height > spriteSheet.getHeight()) return null;
            return spriteSheet.getSubimage(x, y, width, height);
        } catch (IOException e) {
            return null;
        }
    }

    // Loads all player sprites from the sprite sheets
    public void getPlayerImage() {
        try {
            // Sprite sheet configuration
            String idleSheetPath = "res/player/idle.png";
            String walkSheetPath = "res/player/walk.png";
            String runSheetPath = "res/player/run.png";
            int walkSpriteWidth = 29;
            int walkSpriteHeight = 53;
            int walkOffsetX = 15;
            int walkOffsetY = 10;
            int runSpriteWidth = 32;   // Placeholder, update after you provide run details
            int runSpriteHeight = 32;
            int runOffsetX = 0;
            int runOffsetY = 0;
            
            // Load idle animations using exact offsets and 64x64 size
            up1    = getIdleSprite(idleSheetPath, 0,   0,   64, 64);
            up2    = getIdleSprite(idleSheetPath, 64,  0,   64, 64);
            left1  = getIdleSprite(idleSheetPath, 0,   64,  64, 64);
            left2  = getIdleSprite(idleSheetPath, 64,  64,  64, 64);
            down1  = getIdleSprite(idleSheetPath, 0,   128, 64, 64);
            down2  = getIdleSprite(idleSheetPath, 64,  128, 64, 64);
            right1 = getIdleSprite(idleSheetPath, 0,   192, 64, 64);
            right2 = getIdleSprite(idleSheetPath, 64,  192, 64, 64);
            
            // Load walk animations
            up5 = getSprite(walkSheetPath, 0, 0, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            up6 = getSprite(walkSheetPath, 0, 1, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            up7 = getSprite(walkSheetPath, 0, 2, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            up8 = getSprite(walkSheetPath, 0, 3, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            up9 = getSprite(walkSheetPath, 0, 4, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            
            left5 = getSprite(walkSheetPath, 1, 0, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            left6 = getSprite(walkSheetPath, 1, 1, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            left7 = getSprite(walkSheetPath, 1, 2, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            left8 = getSprite(walkSheetPath, 1, 3, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            left9 = getSprite(walkSheetPath, 1, 4, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            
            down5 = getSprite(walkSheetPath, 2, 0, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            down6 = getSprite(walkSheetPath, 2, 1, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            down7 = getSprite(walkSheetPath, 2, 2, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            down8 = getSprite(walkSheetPath, 2, 3, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            down9 = getSprite(walkSheetPath, 2, 4, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            
            right5 = getSprite(walkSheetPath, 3, 0, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            right6 = getSprite(walkSheetPath, 3, 1, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            right7 = getSprite(walkSheetPath, 3, 2, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            right8 = getSprite(walkSheetPath, 3, 3, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            right9 = getSprite(walkSheetPath, 3, 4, walkSpriteWidth, walkSpriteHeight, walkOffsetX, walkOffsetY);
            
            // TODO: Update run loading after you provide its details
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Updates player position and animation state based on input
    public void update() {
        // Always increment spriteCounter
        spriteCounter++;
        // Don't process movement if in dialogue
        if (gp.gameState == GamePanel.DIALOGUE_STATE) {
            return;
        }

        boolean isMoving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;
        boolean isRunning = keyH.shiftPressed && isMoving;
        
        // Update animation state
        if (!isMoving) {
            animationState = "idle";
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else if (isRunning) {
            animationState = "run";
            // TODO: Clamp spriteNum for run animation after details provided
        } else {
            animationState = "walk";
            if (spriteCounter > 12) {
                spriteNum = (spriteNum >= 9) ? 5 : spriteNum + 1;
                spriteCounter = 0;
            }
        }
        
        // Only update if movement keys are pressed
        if (isMoving) {
            // Set direction based on key press
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

            // Check tile collision
            collisionOn = false;
            gp.cCheck.checkTile(this);

            // Move if no collision
            if (!collisionOn) {
                int moveSpeed = isRunning ? speed * 2 : speed;
                switch (direction) {
                    case "up" -> worldY -= moveSpeed;
                    case "down" -> worldY += moveSpeed;
                    case "left" -> worldX -= moveSpeed;
                    case "right" -> worldX += moveSpeed;
                }
            }
        }
        
        // Update stamina
        stamina = gp.hud.getStamina();
        
        // Update HUD
        String weaponName = weapon != null ? weapon.getName() : "No Weapon";
        gp.hud.update(hp, stamina, weaponName, isMoving);
    }
    
    // Draws the player with current sprite based on direction and animation frame
    public void draw(Graphics2D g2){
        BufferedImage image = null;
        switch (animationState) {
            case "idle":
                // Only 2 frames per direction for idle
                if (direction.equals("up")) {
                    image = (spriteNum == 2) ? up2 : up1;
                } else if (direction.equals("down")) {
                    image = (spriteNum == 2) ? down2 : down1;
                } else if (direction.equals("left")) {
                    image = (spriteNum == 2) ? left2 : left1;
                } else if (direction.equals("right")) {
                    image = (spriteNum == 2) ? right2 : right1;
                }
                break;
            case "walk":
                switch(direction){
                    case "up":
                        image = (spriteNum == 5) ? up5 : (spriteNum == 6) ? up6 : (spriteNum == 7) ? up7 : (spriteNum == 8) ? up8 : up9;
                        break;
                    case "down":
                        image = (spriteNum == 5) ? down5 : (spriteNum == 6) ? down6 : (spriteNum == 7) ? down7 : (spriteNum == 8) ? down8 : down9;
                        break;
                    case "left":
                        image = (spriteNum == 5) ? left5 : (spriteNum == 6) ? left6 : (spriteNum == 7) ? left7 : (spriteNum == 8) ? left8 : left9;
                        break;
                    case "right":
                        image = (spriteNum == 5) ? right5 : (spriteNum == 6) ? right6 : (spriteNum == 7) ? right7 : (spriteNum == 8) ? right8 : right9;
                        break;
                }
                break;
            case "run":
                image = null;
                break;
        }
        // Draw the player sprite at screen position and scale to tile size
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
