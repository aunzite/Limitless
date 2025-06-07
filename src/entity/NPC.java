// Blank NPC class, needs sprite and behavior later (Ahmed)

package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;
import java.awt.Font;
import java.awt.Color;

public class NPC extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    public Dialogue dialogue;
    public boolean inDialogue = false;
    private int actionLockCounter = 0;
    private boolean collisionOn = false;
    public Rectangle solidArea;
    public int solidAreaDefaultX;
    public int solidAreaDefaultY;
    private int speed;
    private int spriteCounter = 0;
    private int spriteNum = 1;
    private BufferedImage up1, up2, up3, up4, down1, down2, down3, down4, left1, left2, left3, left4, right1, right2, right3, right4;
    public boolean inRange = false;
    private boolean hasGivenSword = false;
    private int dialogueState = 0;
    private int moveCounter = 0;
    private int moveDirection = 1; // 1 for right, -1 for left
    private int moveDistance = 0;
    private int maxMoveDistance;
    private int startX;
    private boolean isMoving = true;
    private int movementTimer = 0;
    
    public NPC(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        // Initialize Entity fields
        direction = "down";  // Initialize the inherited direction field
        this.speed = 1;  // Set a reasonable speed for NPC movement
        this.playerHitbox = new Rectangle(8, 16, 32, 32); // Match player hitbox size
        this.collisionOn = false;
        
        // Initialize NPC specific fields
        this.dialogue = new Dialogue();
        this.inDialogue = false;
        this.actionLockCounter = 0;
        
        // Set fixed NPC position
        worldX = 496;
        worldY = 256;
        
        getNPCImage();
    }
    
    // Loads all NPC sprites from the sprite sheet
    public void getNPCImage() {
        try {
            // Sprite sheet configuration
            String spriteSheetPath = "res/player/walkNPC.png";  // Changed to walkNPC.png
            int spriteWidth = 39;  // Width of each sprite frame
            int spriteHeight = 50; // Height of each sprite frame
            
            // Load up sprites (row 0)
            up1 = getSprite(spriteSheetPath, 0, 0, spriteWidth, spriteHeight);
            up2 = getSprite(spriteSheetPath, 0, 1, spriteWidth, spriteHeight);
            up3 = getSprite(spriteSheetPath, 0, 2, spriteWidth, spriteHeight);
            up4 = getSprite(spriteSheetPath, 0, 3, spriteWidth, spriteHeight);
            
            // Load down sprites (row 1)
            down1 = getSprite(spriteSheetPath, 1, 0, spriteWidth, spriteHeight);
            down2 = getSprite(spriteSheetPath, 1, 1, spriteWidth, spriteHeight);
            down3 = getSprite(spriteSheetPath, 1, 2, spriteWidth, spriteHeight);
            down4 = getSprite(spriteSheetPath, 1, 3, spriteWidth, spriteHeight);
            
            // Load left sprites (row 2)
            left1 = getSprite(spriteSheetPath, 2, 0, spriteWidth, spriteHeight);
            left2 = getSprite(spriteSheetPath, 2, 1, spriteWidth, spriteHeight);
            left3 = getSprite(spriteSheetPath, 2, 2, spriteWidth, spriteHeight);
            left4 = getSprite(spriteSheetPath, 2, 3, spriteWidth, spriteHeight);
            
            // Load right sprites (row 3)
            right1 = getSprite(spriteSheetPath, 3, 0, spriteWidth, spriteHeight);
            right2 = getSprite(spriteSheetPath, 3, 1, spriteWidth, spriteHeight);
            right3 = getSprite(spriteSheetPath, 3, 2, spriteWidth, spriteHeight);
            right4 = getSprite(spriteSheetPath, 3, 3, spriteWidth, spriteHeight);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Extracts a single sprite from the sprite sheet
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

    // Updates NPC state
    public void update() {
        // Calculate distance to player
        int dx = Math.abs(worldX - gp.player.worldX);
        int dy = Math.abs(worldY - gp.player.worldY);
        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        
        // Check if player is in range for dialogue
        inRange = distance < gp.tileSize * 2;
        
        // Handle dialogue interaction
        if (inRange) {
            // Face the player
            if (gp.player.worldX < worldX) {
                direction = "left";
            } else if (gp.player.worldX > worldX) {
                direction = "right";
            }
            
            // Handle interaction with Enter key
            if (keyH.enterPressed) {
                dialogueState++;
                if (dialogueState == 1) {
                    gp.dialogue.setLine("Defeat the evil pibble to save the world!");
                } else if (dialogueState == 2) {
                    gp.dialogue.setLine("Here, take this sword. It will help you on your journey.");
                    // Give the player a sword
                    gp.player.weapon = new Weapon("Hero's Sword", 15, 1.2, "sword");
                    hasGivenSword = true;
                } else {
                    gp.dialogue.clear();
                    dialogueState = 0; // Reset so you can talk again
                }
                keyH.enterPressed = false;
            }
            return;
        }
        
        // Normal movement pattern
        actionLockCounter++;
        if (actionLockCounter == 120) {
            // Random direction change
            int i = (int) (Math.random() * 4) + 1;
            switch (i) {
                case 1 -> direction = "up";
                case 2 -> direction = "down";
                case 3 -> direction = "left";
                case 4 -> direction = "right";
            }
            actionLockCounter = 0;
        }

        // Check tile collision
        collisionOn = false;
        gp.cCheck.checkTile(this);

        // Move if no collision
        if (!collisionOn) {
            switch (direction) {
                case "up" -> worldY -= speed;
                case "down" -> worldY += speed;
                case "left" -> worldX -= speed;
                case "right" -> worldX += speed;
            }
        }

        // Animation handling
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
    
    // Draw NPC
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        
        // Get the correct sprite based on direction and animation frame
        switch(direction) {
            case "up":
                switch(spriteNum) {
                    case 1: image = up1; break;
                    case 2: image = up2; break;
                    case 3: image = up3; break;
                    case 4: image = up4; break;
                }
                break;
            case "down":
                switch(spriteNum) {
                    case 1: image = left1; break;
                    case 2: image = left2; break;
                    case 3: image = left3; break;
                    case 4: image = left4; break;
                }
                break;
            case "left":
                switch(spriteNum) {
                    case 1: image = down1; break;
                    case 2: image = down2; break;
                    case 3: image = down3; break;
                    case 4: image = down4; break;
                }
                break;
            case "right":
                switch(spriteNum) {
                    case 1: image = right1; break;
                    case 2: image = right2; break;
                    case 3: image = right3; break;
                    case 4: image = right4; break;
                }
                break;
        }
        
        // Draw the NPC
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        // Draw interaction message if player is in range
        if (inRange) {
            // Set up the message
            String message = "Press Enter to chat";
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            
            // Get the width of the message for centering
            int messageWidth = g2.getFontMetrics().stringWidth(message);
            
            // Draw semi-transparent background
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(
                screenX + (gp.tileSize - messageWidth) / 2 - 5,  // x position
                screenY - 30,                                    // y position (above NPC)
                messageWidth + 10,                              // width with padding
                25,                                             // height
                10,                                             // arc width
                10                                              // arc height
            );
            
            // Draw the message
            g2.setColor(Color.WHITE);
            g2.drawString(
                message,
                screenX + (gp.tileSize - messageWidth) / 2,  // center the text
                screenY - 10                                 // position above NPC
            );
        }
    }
}
