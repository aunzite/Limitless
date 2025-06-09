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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.awt.FontMetrics;

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
    private BufferedImage up1, up2, up3, up4, down1, down2, down3, down4, left1, left2, left3, left4, right1, right2, right3, right4;
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
            int spriteWidth = 39;
            int spriteHeight = 50;
            
            BufferedImage spriteSheet = ImageIO.read(new File(spriteSheetPath));
            if (spriteSheet == null) {
                throw new IOException("Failed to load sprite sheet");
            }
            
            // Load all sprites in a more efficient way
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    BufferedImage sprite = spriteSheet.getSubimage(
                        (col * spriteWidth + 9) + (col*25),
                        (row * spriteHeight + 14) + (row*13),
                        spriteWidth,
                        spriteHeight
                    );
                    
                    // Assign sprite to appropriate variable based on row and column
                    switch(row) {
                        case 0 -> {
                            switch(col) {
                                case 0 -> up1 = sprite;
                                case 1 -> up2 = sprite;
                                case 2 -> up3 = sprite;
                                case 3 -> up4 = sprite;
                            }
                        }
                        case 1 -> {
                            switch(col) {
                                case 0 -> left1 = sprite;
                                case 1 -> left2 = sprite;
                                case 2 -> left3 = sprite;
                                case 3 -> left4 = sprite;
                            }
                        }
                        case 2 -> {
                            switch(col) {
                                case 0 -> down1 = sprite;
                                case 1 -> down2 = sprite;
                                case 2 -> down3 = sprite;
                                case 3 -> down4 = sprite;
                            }
                        }
                        case 3 -> {
                            switch(col) {
                                case 0 -> right1 = sprite;
                                case 1 -> right2 = sprite;
                                case 2 -> right3 = sprite;
                                case 3 -> right4 = sprite;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading NPC sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStory() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/dialogue/elaria.txt"));
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
                return;
            }
            
            // Handle dialogue progression with enter key
            if (keyH.enterPressed) {
                handleDialogue();
                keyH.enterPressed = false;
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
                String command = weaponCommand.substring(1, weaponCommand.length() - 1);
                String weaponName = command.substring(8).replace("\"", "").trim();
                gp.player.weapon = new Weapon(weaponName, 25, 1.0, "sword");
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
            
            // Handle interaction with Enter key
            if (keyH.enterPressed) {
                handleDialogue();
                keyH.enterPressed = false;
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
        BufferedImage image = getCurrentSprite();
        
        // Draw the NPC
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        // Draw interaction message if player is in range
        if (inRange) {
            if (inDialogue) {
                drawDialogueBox(g2);
            } else {
                drawInteractionMessage(g2, screenX, screenY);
            }
        }
    }
    
    private BufferedImage getCurrentSprite() {
        return switch(direction) {
            case "up" -> switch(spriteNum) {
                case 1 -> up1;
                case 2 -> up2;
                case 3 -> up3;
                case 4 -> up4;
                default -> up1;
            };
            case "down" -> switch(spriteNum) {
                case 1 -> down1;
                case 2 -> down2;
                case 3 -> down3;
                case 4 -> down4;
                default -> down1;
            };
            case "left" -> switch(spriteNum) {
                case 1 -> left1;
                case 2 -> left2;
                case 3 -> left3;
                case 4 -> left4;
                default -> left1;
            };
            case "right" -> switch(spriteNum) {
                case 1 -> right1;
                case 2 -> right2;
                case 3 -> right3;
                case 4 -> right4;
                default -> right1;
            };
            default -> down1;
        };
    }
    
    private void drawInteractionMessage(Graphics2D g2, int screenX, int screenY) {
        String message = "Press Enter to chat";
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Get the width of the message for centering
        int messageWidth = g2.getFontMetrics().stringWidth(message);
        
        // Draw semi-transparent background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(
            screenX + (gp.tileSize - messageWidth) / 2 - 5,
            screenY - 30,
            messageWidth + 10,
            25,
            10,
            10
        );
        
        // Draw the message
        g2.setColor(Color.WHITE);
        g2.drawString(
            message,
            screenX + (gp.tileSize - messageWidth) / 2,
            screenY - 10
        );
    }

    private void drawDialogueBox(Graphics2D g2) {
        int boxX = 60;
        int boxW = gp.screenWidth - 120;
        int boxH = 140; // Taller height
        int boxY = gp.screenHeight - boxH - 40;
        g2.setColor(new Color(0,0,0,220));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        g2.setColor(Color.WHITE);
        g2.setStroke(new java.awt.BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        String para = (currentParagraph < paragraphs.size()) ? paragraphs.get(currentParagraph) : "";
        boolean isBracketed = para.trim().startsWith("[") && para.trim().endsWith("]");
        boolean isQuoted = para.contains("\"");
        if (!isBracketed) {
            // Draw Elaria's sprite and name if not bracketed
            int spriteSize = gp.tileSize * 2;
            int spriteX = boxX + 10;
            int spriteY = boxY - spriteSize/2;
            BufferedImage elariaSprite = getCurrentSprite();
            g2.drawImage(elariaSprite, spriteX, spriteY, spriteSize, spriteSize, null);
            Font nameFont = new Font("Comic Sans MS", Font.BOLD, 28);
            if (nameFont == null) nameFont = new Font("Arial", Font.BOLD, 28);
            g2.setFont(nameFont);
            g2.drawString(npcName, boxX + spriteSize + 20, boxY + 38);
            Font dialogueFont = new Font("Comic Sans MS", Font.PLAIN, 22);
            if (dialogueFont == null) dialogueFont = new Font("Arial", Font.PLAIN, 22);
            g2.setFont(dialogueFont);
            drawStringMultiLine(g2, visibleText.toString(), boxX + spriteSize + 20, boxY + 70, boxW - spriteSize - 40);
        } else {
            // Only draw the bracketed text, centered in the box
            Font bracketFont = new Font("Comic Sans MS", Font.ITALIC, 22);
            if (bracketFont == null) bracketFont = new Font("Arial", Font.ITALIC, 22);
            g2.setFont(bracketFont);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(visibleText.toString());
            int textX = boxX + (boxW - textWidth) / 2;
            int textY = boxY + boxH / 2 + fm.getAscent() / 2;
            g2.drawString(visibleText.toString(), textX, textY);
        }

        // Draw the continue text with pulsing effect
        if (inDialogue && currentParagraph < paragraphs.size()) {
            int alpha = (int)(128 + 127 * Math.sin(System.currentTimeMillis() / 200.0));
            g2.setColor(new Color(255, 255, 255, alpha));
            g2.setFont(new Font("Arial", Font.ITALIC, 16));
            String continueText = "Press Enter to continue";
            int textWidth = g2.getFontMetrics().stringWidth(continueText);
            // Position in bottom right of dialogue box
            g2.drawString(continueText, boxX + boxW - textWidth - 20, boxY + boxH - 20);
        }
    }

    private void drawStringMultiLine(Graphics2D g2, String text, int x, int y, int width) {
        Font font = g2.getFont();
        FontMetrics metrics = g2.getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curY = y;
        for (String word : words) {
            String testLine = line + word + " ";
            int testWidth = metrics.stringWidth(testLine);
            if (testWidth > width) {
                g2.drawString(line.toString(), x, curY);
                line = new StringBuilder(word + " ");
                curY += lineHeight;
            } else {
                line.append(word).append(" ");
            }
        }
        if (line.length() > 0) {
            g2.drawString(line.toString(), x, curY);
        }
    }
}
