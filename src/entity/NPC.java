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
        BufferedImage image = getCurrentSprite();
        
        // Calculate screen position
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        
        // Draw the NPC at 1.3x the tile size, feet aligned
        double scale = 1.3;
        int drawWidth = (int)(gp.tileSize * scale);
        int drawHeight = (int)(gp.tileSize * scale);
        int drawX = screenX - (drawWidth - gp.tileSize) / 2;
        int drawY = screenY - (drawHeight - gp.tileSize);
        g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);

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
                case 5 -> up5;
                case 6 -> up6;
                case 7 -> up7;
                case 8 -> up8;
                case 9 -> up9;
                default -> up1;
            };
            case "down" -> switch(spriteNum) {
                case 1 -> down1;
                case 2 -> down2;
                case 3 -> down3;
                case 4 -> down4;
                case 5 -> down5;
                case 6 -> down6;
                case 7 -> down7;
                case 8 -> down8;
                case 9 -> down9;
                default -> down1;
            };
            case "left" -> switch(spriteNum) {
                case 1 -> left1;
                case 2 -> left2;
                case 3 -> left3;
                case 4 -> left4;
                case 5 -> left5;
                case 6 -> left6;
                case 7 -> left7;
                case 8 -> left8;
                case 9 -> left9;
                default -> left1;
            };
            case "right" -> switch(spriteNum) {
                case 1 -> right1;
                case 2 -> right2;
                case 3 -> right3;
                case 4 -> right4;
                case 5 -> right5;
                case 6 -> right6;
                case 7 -> right7;
                case 8 -> right8;
                case 9 -> right9;
                default -> right1;
            };
            default -> down1;
        };
    }
    
    private void drawInteractionMessage(Graphics2D g2, int screenX, int screenY) {
        String message = "Press E to chat";
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        
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
            g2.setFont(nameFont);
            g2.drawString(npcName, boxX + spriteSize + 20, boxY + 38);
            Font dialogueFont = new Font("Comic Sans MS", Font.PLAIN, 22);
            g2.setFont(dialogueFont);
            drawStringMultiLine(g2, visibleText.toString(), boxX + spriteSize + 20, boxY + 70, boxW - spriteSize - 40);
        } else {
            // Only draw the bracketed text, centered in the box
            Font bracketFont = new Font("Comic Sans MS", Font.ITALIC, 22);
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

            g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 16));
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
