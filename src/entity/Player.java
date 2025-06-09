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
    public Inventory inventory; // Add inventory field

    // Screen position constants (center of screen)
    public final int screenX; // Fixed X position on screen
    public final int screenY; // Fixed Y position on screen

    // Animation state
    private String animationState = "idle"; // Can be "idle", "walk", or "run"

    // Run animation frames (10-17 for each direction)
    public BufferedImage up10, up11, up12, up13, up14, up15, up16, up17;
    public BufferedImage down10, down11, down12, down13, down14, down15, down16, down17;
    public BufferedImage left10, left11, left12, left13, left14, left15, left16, left17;
    public BufferedImage right10, right11, right12, right13, right14, right15, right16, right17;

    // Store last collided tile(s) for debug drawing
    public java.util.List<int[]> lastCollisionTiles = new java.util.ArrayList<>();
    public void setLastCollisionTile(int col, int row) {
        lastCollisionTiles.add(new int[]{col, row});
    }

    // Constructor initializes player with game panel and keyboard handler
    public Player (GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;

        hp = 100;
        stamina = 100;
        weapon = null; // Start with no weapon
        inventory = new Inventory(gp); // Use new Inventory constructor

        // Calculate center position of screen for player
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);
       

        playerHitbox = new Rectangle(24, 0, 32, 88);

        setDefaultValues();
        getPlayerImage();
    }

    // Sets default values for player position and movement
    public void setDefaultValues (){
        // Set initial world position to center of the map (top-left of sprite)
        worldX = gp.tileSize * 12;
        worldY = gp.tileSize * 10;
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

    // Helper for walk sprites using exact pixel offsets
    private BufferedImage getWalkSprite(String sheetPath, int x, int y) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));
            if (spriteSheet == null) return null;
            if (x + 64 > spriteSheet.getWidth() || y + 64 > spriteSheet.getHeight()) return null;
            return spriteSheet.getSubimage(x, y, 64, 64);
        } catch (IOException e) {
            return null;
        }
    }

    // Helper for run sprites using exact pixel offsets
    private BufferedImage getRunSprite(String sheetPath, int x, int y) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));
            if (spriteSheet == null) return null;
            if (x + 64 > spriteSheet.getWidth() || y + 64 > spriteSheet.getHeight()) return null;
            return spriteSheet.getSubimage(x, y, 64, 64);
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
            
            // Load walk animations using exact offsets and 64x64 size
            // Up (row 1)
            up5 = getWalkSprite(walkSheetPath, 0, 0);
            up6 = getWalkSprite(walkSheetPath, 64, 0);
            up7 = getWalkSprite(walkSheetPath, 128, 0);
            up8 = getWalkSprite(walkSheetPath, 192, 0);
            up9 = getWalkSprite(walkSheetPath, 256, 0);
            // Left (row 2)
            left5 = getWalkSprite(walkSheetPath, 0, 64);
            left6 = getWalkSprite(walkSheetPath, 64, 64);
            left7 = getWalkSprite(walkSheetPath, 128, 64);
            left8 = getWalkSprite(walkSheetPath, 192, 64);
            left9 = getWalkSprite(walkSheetPath, 256, 64);
            // Down (row 3)
            down5 = getWalkSprite(walkSheetPath, 0, 128);
            down6 = getWalkSprite(walkSheetPath, 64, 128);
            down7 = getWalkSprite(walkSheetPath, 128, 128);
            down8 = getWalkSprite(walkSheetPath, 192, 128);
            down9 = getWalkSprite(walkSheetPath, 256, 128);
            // Right (row 4)
            right5 = getWalkSprite(walkSheetPath, 0, 192);
            right6 = getWalkSprite(walkSheetPath, 64, 192);
            right7 = getWalkSprite(walkSheetPath, 128, 192);
            right8 = getWalkSprite(walkSheetPath, 192, 192);
            right9 = getWalkSprite(walkSheetPath, 256, 192);
            
            // Load run animations using exact offsets and 64x64 size
            // Up (row 1)
            BufferedImage runUp[] = new BufferedImage[8];
            for (int i = 0; i < 8; i++) {
                runUp[i] = getRunSprite(runSheetPath, i * 64, 0);
            }
            // Left (row 2)
            BufferedImage runLeft[] = new BufferedImage[8];
            for (int i = 0; i < 8; i++) {
                runLeft[i] = getRunSprite(runSheetPath, i * 64, 64);
            }
            // Down (row 3)
            BufferedImage runDown[] = new BufferedImage[8];
            for (int i = 0; i < 8; i++) {
                runDown[i] = getRunSprite(runSheetPath, i * 64, 128);
            }
            // Right (row 4)
            BufferedImage runRight[] = new BufferedImage[8];
            for (int i = 0; i < 8; i++) {
                runRight[i] = getRunSprite(runSheetPath, i * 64, 192);
            }
            // Assign to class fields for run animation (run5-run12)
            up10 = runUp[0]; up11 = runUp[1]; up12 = runUp[2]; up13 = runUp[3]; up14 = runUp[4]; up15 = runUp[5]; up16 = runUp[6]; up17 = runUp[7];
            left10 = runLeft[0]; left11 = runLeft[1]; left12 = runLeft[2]; left13 = runLeft[3]; left14 = runLeft[4]; left15 = runLeft[5]; left16 = runLeft[6]; left17 = runLeft[7];
            down10 = runDown[0]; down11 = runDown[1]; down12 = runDown[2]; down13 = runDown[3]; down14 = runDown[4]; down15 = runDown[5]; down16 = runDown[6]; down17 = runDown[7];
            right10 = runRight[0]; right11 = runRight[1]; right12 = runRight[2]; right13 = runRight[3]; right14 = runRight[4]; right15 = runRight[5]; right16 = runRight[6]; right17 = runRight[7];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Updates player position and animation state based on input
    public void update() {
        // Handle inventory toggle/close
        if (keyH.iPressed) {
            inventory.toggle();
            keyH.iPressed = false;
        }

        // Don't process movement if inventory is open
        if (inventory.isOpen()) {
            return;
        }

        // Always increment spriteCounter
        spriteCounter++;
        // Don't process movement if in dialogue
        if (gp.gameState == GamePanel.DIALOGUE_STATE) {
            return;
        }

        // Clear last collision tiles before movement
        lastCollisionTiles.clear();

        boolean isMoving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;
        boolean isRunning = keyH.shiftPressed && isMoving;
        
        // Track previous animation state
        String prevAnimationState = animationState;
        // Update animation state
        if (!isMoving) {
            animationState = "idle";
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else if (isRunning && stamina > 0) {
            animationState = "run";
            if (!prevAnimationState.equals("run")) {
                spriteNum = 10; // Start at first run frame
            }
            if (spriteCounter > 12) {
                spriteNum = (spriteNum >= 17) ? 10 : spriteNum + 1;
                spriteCounter = 0;
            }
        } else {
            animationState = "walk";
            if (!prevAnimationState.equals("walk")) {
                spriteNum = 5; // Start at first walk frame
            }
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
            // Only allow running if stamina is above 0
            int moveSpeed = (isRunning && stamina > 0) ? speed * 2 : speed;
            for (int i = 0; i < moveSpeed; i++) {
                int prevX = worldX;
                int prevY = worldY;
                switch (direction) {
                    case "up" -> worldY--;
                    case "down" -> worldY++;
                    case "left" -> worldX--;
                    case "right" -> worldX++;
                }
                collisionOn = false;
                gp.cCheck.checkTile(this);
                gp.cCheck.checkEntity(this, gp.npc);
                if (collisionOn) {
                    worldX = prevX;
                    worldY = prevY;
                    break;
                }
            }
        }
        
        // Update stamina
        if (isRunning && stamina > 0) {
            gp.hud.drainStamina();
        } else {
            gp.hud.regenerateStamina(isMoving);
        }
        stamina = gp.hud.getStamina();
        
        // Update HUD
        String weaponName = weapon != null ? weapon.getName() : "No Weapon";
        gp.hud.update(hp, stamina, weaponName, isMoving);
    }
    
    // Draws the player with current sprite based on direction and animation frame
    public void draw(Graphics2D g2){
        // Draw inventory overlay if open
        if (inventory.isOpen()) {
            // Draw 50% opacity black rectangle over everything
            g2.setColor(new java.awt.Color(0, 0, 0, 128));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            // Draw inventory and items on top
            inventory.draw(g2);
            // Always draw HUD on top of inventory
            String weaponName = weapon != null ? weapon.getName() : "No Weapon";
            gp.hud.update(hp, stamina, weaponName, false);
            gp.hud.draw(g2, weapon);
            return; // Don't draw player or anything else
        }

        // Draw player sprite
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
                // 8 frames per direction for run (10-17)
                switch(direction){
                    case "up":
                        switch(spriteNum) {
                            case 10: image = up10; break;
                            case 11: image = up11; break;
                            case 12: image = up12; break;
                            case 13: image = up13; break;
                            case 14: image = up14; break;
                            case 15: image = up15; break;
                            case 16: image = up16; break;
                            case 17: image = up17; break;
                        }
                        break;
                    case "down":
                        switch(spriteNum) {
                            case 10: image = down10; break;
                            case 11: image = down11; break;
                            case 12: image = down12; break;
                            case 13: image = down13; break;
                            case 14: image = down14; break;
                            case 15: image = down15; break;
                            case 16: image = down16; break;
                            case 17: image = down17; break;
                        }
                        break;
                    case "left":
                        switch(spriteNum) {
                            case 10: image = left10; break;
                            case 11: image = left11; break;
                            case 12: image = left12; break;
                            case 13: image = left13; break;
                            case 14: image = left14; break;
                            case 15: image = left15; break;
                            case 16: image = left16; break;
                            case 17: image = left17; break;
                        }
                        break;
                    case "right":
                        switch(spriteNum) {
                            case 10: image = right10; break;
                            case 11: image = right11; break;
                            case 12: image = right12; break;
                            case 13: image = right13; break;
                            case 14: image = right14; break;
                            case 15: image = right15; break;
                            case 16: image = right16; break;
                            case 17: image = right17; break;
                        }
                        break;
                }
                break;
        }
        // Draw the player sprite at 1.3x the tile size, feet aligned
        double scale = 1.3;
        int drawWidth = (int)(gp.tileSize * scale);
        int drawHeight = (int)(gp.tileSize * scale);
        int drawX = screenX - drawWidth / 2 + gp.tileSize / 2;
        int drawY = screenY - drawHeight + gp.tileSize;
        g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
    }
}
