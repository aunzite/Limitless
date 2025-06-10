// Blank NPC class, needs sprite and behavior later (Ahmed)

package entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;
import java.awt.BasicStroke;

public class NPC extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    public Dialogue dialogue;
    public boolean inDialogue = false;
    private int actionLockCounter = 0;
    private boolean collisionOn = false;
    private int speed;
    private int spriteCounter = 0;
    private int spriteNum = 1;
    private BufferedImage up1, up2, up3, up4, up5, up6, up7, up8, up9, down1, down2, down3, down4, down5, down6, down7, down8, down9, left1, left2, left3, left4, left5, left6, left7, left8, left9, right1, right2, right3, right4, right5, right6, right7, right8, right9;
    public boolean inRange = false;
    private boolean hasGivenSword = false;
    private int dialogueState = 0;
    private List<String> paragraphs = new ArrayList<>();
    private int currentParagraph = 0;
    private StringBuilder visibleText = new StringBuilder();
    private int scrollIndex = 0;
    private long lastScrollTime = 0;
    private static final int SCROLL_DELAY = 30; // ms per character (medium speed)
    private boolean paragraphFullyShown = false;
    private final String npcName = "Elaria";
    private String weaponCommand = null; // Store weapon command separately
    private long lastInteractionTime = 0;  // Track last interaction time
    private static final long INTERACTION_COOLDOWN = 1000;  // 1 second cooldown in milliseconds
    
    public NPC(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        // Initialize Entity fields
        direction = "down";  // Initialize the inherited direction field
        this.speed = 1;  // Set a reasonable speed for NPC movement
        // Hitbox: exactly matches player
        this.playerHitbox = new Rectangle(24, 0, 32, 88);
        this.collisionOn = false;
        
        // Initialize NPC specific fields
        this.dialogue = new Dialogue();
        this.inDialogue = false;
        this.actionLockCounter = 0;
        
        // Set fixed NPC position - moved even lower on the path
        worldX = 496;
        worldY = 512; // Changed from 384 to 512 to move NPC even lower (3 more tiles down)
        
        getNPCImage();
        loadStory();
    }
    
    // Loads all NPC sprites from the sprite sheet
    public void getNPCImage() {
        try {
            // Sprite sheet configuration
            String spriteSheetPath = "res/elaria/walk.png";
            
            BufferedImage spriteSheet = ImageIO.read(new File(spriteSheetPath));
            if (spriteSheet == null) {
                throw new IOException("Failed to load sprite sheet");
            }
            
            // Load all sprites using exact pixel offsets
            // Up (row 1)
            up1 = spriteSheet.getSubimage(0, 0, 64, 64);
            up2 = spriteSheet.getSubimage(64, 0, 64, 64);
            up3 = spriteSheet.getSubimage(128, 0, 64, 64);
            up4 = spriteSheet.getSubimage(192, 0, 64, 64);
            up5 = spriteSheet.getSubimage(256, 0, 64, 64);
            up6 = spriteSheet.getSubimage(320, 0, 64, 64);
            up7 = spriteSheet.getSubimage(384, 0, 64, 64);
            up8 = spriteSheet.getSubimage(448, 0, 64, 64);
            up9 = spriteSheet.getSubimage(512, 0, 64, 64);
            
            // Left (row 2)
            left1 = spriteSheet.getSubimage(0, 64, 64, 64);
            left2 = spriteSheet.getSubimage(64, 64, 64, 64);
            left3 = spriteSheet.getSubimage(128, 64, 64, 64);
            left4 = spriteSheet.getSubimage(192, 64, 64, 64);
            left5 = spriteSheet.getSubimage(256, 64, 64, 64);
            left6 = spriteSheet.getSubimage(320, 64, 64, 64);
            left7 = spriteSheet.getSubimage(384, 64, 64, 64);
            left8 = spriteSheet.getSubimage(448, 64, 64, 64);
            left9 = spriteSheet.getSubimage(512, 64, 64, 64);
            
            // Down (row 3)
            down1 = spriteSheet.getSubimage(0, 128, 64, 64);
            down2 = spriteSheet.getSubimage(64, 128, 64, 64);
            down3 = spriteSheet.getSubimage(128, 128, 64, 64);
            down4 = spriteSheet.getSubimage(192, 128, 64, 64);
            down5 = spriteSheet.getSubimage(256, 128, 64, 64);
            down6 = spriteSheet.getSubimage(320, 128, 64, 64);
            down7 = spriteSheet.getSubimage(384, 128, 64, 64);
            down8 = spriteSheet.getSubimage(448, 128, 64, 64);
            down9 = spriteSheet.getSubimage(512, 128, 64, 64);
            
            // Right (row 4)
            right1 = spriteSheet.getSubimage(0, 192, 64, 64);
            right2 = spriteSheet.getSubimage(64, 192, 64, 64);
            right3 = spriteSheet.getSubimage(128, 192, 64, 64);
            right4 = spriteSheet.getSubimage(192, 192, 64, 64);
            right5 = spriteSheet.getSubimage(256, 192, 64, 64);
            right6 = spriteSheet.getSubimage(320, 192, 64, 64);
            right7 = spriteSheet.getSubimage(384, 192, 64, 64);
            right8 = spriteSheet.getSubimage(448, 192, 64, 64);
            right9 = spriteSheet.getSubimage(512, 192, 64, 64);
            
        } catch (IOException e) {
            System.err.println("Error loading NPC sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStory() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("res/dialogue/elaria.txt"));
            StringBuilder paragraph = new StringBuilder();
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    if (paragraph.length() > 0) {
                        paragraphs.add(paragraph.toString().trim());
                        paragraph.setLength(0);
                    }
                } else if (!line.startsWith("Elaria") && !(line.trim().startsWith("[") && line.trim().endsWith("]"))) {
                    paragraph.append(line).append(" ");
                } else if (line.trim().startsWith("[") && line.trim().endsWith("]")) {
                    // Handle all bracketed text as special commands
                    if (line.trim().startsWith("[Weapon:")) {
                        // Store weapon command separately without adding to dialogue
                        weaponCommand = line.trim();
                    } else {
                        // Add other bracketed text to dialogue
                        paragraphs.add(line.trim());
                    }
                }
            }
            if (paragraph.length() > 0) {
                paragraphs.add(paragraph.toString().trim());
            }
            
            // Remove any empty paragraphs that might have been added
            paragraphs.removeIf(String::isEmpty);
        } catch (IOException e) {
            paragraphs.add("[Story file missing]");
        }
    }

    // Updates NPC state
    public void update() {
        // If in dialogue, only handle dialogue progression
        if (inDialogue) {
            direction = "down"; // Face forward during dialogue
            
            // Handle escape key to skip dialogue
            if (keyH.escapePressed) {
                inDialogue = false;
                currentParagraph = 0;
                gp.gameState = GamePanel.PLAY_STATE;
                keyH.escapePressed = false;
                lastInteractionTime = System.currentTimeMillis();  // Set cooldown when dialogue ends
                return;
            }
            
            // Handle dialogue progression with enter key
            if (keyH.ePressed) {
                handleDialogue();
                keyH.ePressed = false;
            }

            // Scrolling effect
            if (!paragraphFullyShown && currentParagraph < paragraphs.size()) {
                long now = System.currentTimeMillis();
                if (now - lastScrollTime > SCROLL_DELAY) {
                    if (scrollIndex < paragraphs.get(currentParagraph).length()) {
                        visibleText.append(paragraphs.get(currentParagraph).charAt(scrollIndex));
                        scrollIndex++;
                        lastScrollTime = now;
                    } else {
                        paragraphFullyShown = true;
                    }
                }
            }
            
            // Handle weapon giving at the right moment
            if (currentParagraph == 12 && weaponCommand != null && !hasGivenSword) {
                // Add Solthorn object to inventory as an Item
                gp.player.inventory.addItem(new Item("Solthorn", "res/object/solthorn.png", 1));
                hasGivenSword = true;
            }
            return;
        }

        // Calculate distance to player
        int dx = Math.abs(worldX - gp.player.worldX);
        int dy = Math.abs(worldY - gp.player.worldY);
        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        
        // Check if player is in range for dialogue
        inRange = distance < gp.tileSize * 2;
        
        // Handle dialogue interaction
        if (inRange) {
            // Face the player based on relative position
            if (gp.player.worldY < worldY - gp.tileSize) {
                direction = "up";
            } else if (gp.player.worldY > worldY + gp.tileSize) {
                direction = "down";
            } else if (gp.player.worldX < worldX) {
                direction = "left";
            } else {
                direction = "right";
            }
            
            // Check cooldown before allowing interaction
            long currentTime = System.currentTimeMillis();
            if (keyH.ePressed && currentTime - lastInteractionTime >= INTERACTION_COOLDOWN) {
                handleDialogue();
                keyH.ePressed = false;
            }
            return;
        }
        
        // Normal movement pattern
        actionLockCounter++;
        if (actionLockCounter >= 120) {
            // Random direction change
            String[] directions = {"up", "down", "left", "right"};
            direction = directions[(int) (Math.random() * 4)];
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
            spriteNum = spriteNum == 4 ? 1 : spriteNum + 1;
            spriteCounter = 0;
        }
    }
    
    private void handleDialogue() {
        if (!inDialogue) {
            // Start dialogue
            inDialogue = true;
            currentParagraph = 0;
            visibleText.setLength(0);
            scrollIndex = 0;
            paragraphFullyShown = false;
            lastScrollTime = System.currentTimeMillis();
            gp.gameState = GamePanel.DIALOGUE_STATE;
        } else {
            // If typewriter effect is running, skip to full text
            if (!paragraphFullyShown) {
                visibleText = new StringBuilder(paragraphs.get(currentParagraph));
                scrollIndex = paragraphs.get(currentParagraph).length();
                paragraphFullyShown = true;
            } else {
                // Move to next paragraph
                currentParagraph++;
                if (currentParagraph >= paragraphs.size()) {
                    // End dialogue
                    inDialogue = false;
                    currentParagraph = 0;
                    gp.gameState = GamePanel.PLAY_STATE;
                    lastInteractionTime = System.currentTimeMillis();  // Set cooldown when dialogue actually ends
                } else {
                    // Start next paragraph
                    visibleText.setLength(0);
                    scrollIndex = 0;
                    paragraphFullyShown = false;
                    lastScrollTime = System.currentTimeMillis();
                }
            }
        }
    }
    
    // Draw NPC
    public void draw(Graphics2D g2) {
        // Draw NPC sprite
        BufferedImage image = null;
        switch (direction) {
            case "up": image = up1; break;
            case "down": image = down1; break;
            case "left": image = left1; break;
            case "right": image = right1; break;
        }
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        // Draw dialogue box if in dialogue
        if (inDialogue) {
            // Draw dialogue box
            int boxX = 50;
            int boxY = gp.screenHeight - 200;
            int boxWidth = gp.screenWidth - 100;
            int boxHeight = 150;
            
            // Draw semi-transparent black background
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
            
            // Draw white border
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
            
            // Draw text
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
            g2.setColor(Color.WHITE);
            
            // Draw visible text with word wrap
            String text = visibleText.toString();
            String[] words = text.split(" ");
            int x = boxX + 20;
            int y = boxY + 40;
            int lineWidth = 0;
            int maxWidth = boxWidth - 40;
            
            for (String word : words) {
                int wordWidth = g2.getFontMetrics().stringWidth(word + " ");
                if (lineWidth + wordWidth > maxWidth) {
                    y += 30;
                    x = boxX + 20;
                    lineWidth = 0;
                }
                g2.drawString(word + " ", x + lineWidth, y);
                lineWidth += wordWidth;
            }
            
            // Draw "Press E to continue" text
            if (paragraphFullyShown) {
                g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 20));
                String continueText = "Press E to continue";
                int textWidth = g2.getFontMetrics().stringWidth(continueText);
                g2.drawString(continueText, boxX + (boxWidth - textWidth) / 2, boxY + boxHeight - 20);
            }
        }
    }
}
