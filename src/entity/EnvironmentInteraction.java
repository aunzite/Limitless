package entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import main.GamePanel;
import main.KeyHandler;

public class EnvironmentInteraction {
    GamePanel gp;
    KeyHandler keyH;
    private boolean inRange = false;
    private boolean inDialogue = false;
    private int currentParagraph = 0;
    private String[] paragraphs;
    private StringBuilder visibleText = new StringBuilder();
    private int scrollIndex = 0;
    private long lastScrollTime = 0;
    private static final int SCROLL_DELAY = 30;
    private boolean paragraphFullyShown = false;
    private String interactionName;
    private int worldX;
    private int worldY;
    private int interactionRadius;
    
    public EnvironmentInteraction(GamePanel gp, KeyHandler keyH, String name, int x, int y, int radius, String[] dialogue) {
        this.gp = gp;
        this.keyH = keyH;
        this.interactionName = name;
        this.worldX = x;
        this.worldY = y;
        this.interactionRadius = radius;
        this.paragraphs = dialogue;
    }
    
    public void update() {
        // If in dialogue, only handle dialogue progression
        if (inDialogue) {
            // Handle escape key to skip dialogue
            if (keyH.escapePressed) {
                inDialogue = false;
                currentParagraph = 0;
                gp.gameState = GamePanel.PLAY_STATE;
                keyH.escapePressed = false;
                return;
            }
            
            // Handle dialogue progression with enter key
            if (keyH.ePressed) {
                handleDialogue();
                keyH.ePressed = false;
            }

            // Scrolling effect
            if (!paragraphFullyShown && currentParagraph < paragraphs.length) {
                long now = System.currentTimeMillis();
                if (now - lastScrollTime > SCROLL_DELAY) {
                    if (scrollIndex < paragraphs[currentParagraph].length()) {
                        visibleText.append(paragraphs[currentParagraph].charAt(scrollIndex));
                        scrollIndex++;
                        lastScrollTime = now;
                    } else {
                        paragraphFullyShown = true;
                    }
                }
            }
            return;
        }

        // Calculate distance to player
        int dx = Math.abs(worldX - gp.player.worldX);
        int dy = Math.abs(worldY - gp.player.worldY);
        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        
        // Check if player is in range for dialogue
        inRange = distance < interactionRadius;
        
        // Handle dialogue interaction
        if (inRange && keyH.ePressed) {
            handleDialogue();
            keyH.ePressed = false;
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
                visibleText = new StringBuilder(paragraphs[currentParagraph]);
                scrollIndex = paragraphs[currentParagraph].length();
                paragraphFullyShown = true;
            } else {
                // Move to next paragraph
                currentParagraph++;
                if (currentParagraph >= paragraphs.length) {
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
    
    public void draw(Graphics2D g2) {
        // Calculate screen position
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        
        // Draw interaction message if player is in range
        if (inRange) {
            if (inDialogue) {
                drawDialogueBox(g2);
            } else {
                drawInteractionMessage(g2, screenX, screenY);
            }
        }
    }
    
    private void drawInteractionMessage(Graphics2D g2, int screenX, int screenY) {
<<<<<<< HEAD
        String message = "Press Enter to interact";
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
=======
        String message = "Press E to interact";
        g2.setFont(new Font("Arial", Font.BOLD, 16));
>>>>>>> 85801160563dc370b0ef2d3376d91afa7643393b
        
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
        int boxH = 140;
        int boxY = gp.screenHeight - boxH - 40;
        
        // Draw dialogue box background
        g2.setColor(new Color(0,0,0,220));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        g2.setColor(Color.WHITE);
        g2.setStroke(new java.awt.BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        
        // Draw interaction name
        Font nameFont = new Font("Comic Sans MS", Font.BOLD, 28);
        g2.setFont(nameFont);
        g2.drawString(interactionName, boxX + 20, boxY + 38);
        
        // Draw dialogue text
        Font dialogueFont = new Font("Comic Sans MS", Font.PLAIN, 22);
        g2.setFont(dialogueFont);
        drawStringMultiLine(g2, visibleText.toString(), boxX + 20, boxY + 70, boxW - 40);
        
        // Draw continue text with pulsing effect
        if (inDialogue && currentParagraph < paragraphs.length) {
            int alpha = (int)(128 + 127 * Math.sin(System.currentTimeMillis() / 200.0));
            g2.setColor(new Color(255, 255, 255, alpha));
<<<<<<< HEAD
            g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 16));
            String continueText = "Press Enter to continue";
=======
            g2.setFont(new Font("Arial", Font.ITALIC, 16));
            String continueText = "Press E to continue";
>>>>>>> 85801160563dc370b0ef2d3376d91afa7643393b
            int textWidth = g2.getFontMetrics().stringWidth(continueText);
            g2.drawString(continueText, boxX + boxW - textWidth - 20, boxY + boxH - 20);
        }
    }
    
    private void drawStringMultiLine(Graphics2D g2, String text, int x, int y, int maxWidth) {
        String[] words = text.split(" ");
        String currentLine = "";
        
        for (String word : words) {
            if (g2.getFontMetrics().stringWidth(currentLine + " " + word) < maxWidth) {
                currentLine += word + " ";
            } else {
                g2.drawString(currentLine, x, y);
                currentLine = word + " ";
                y += 30;
            }
        }
        g2.drawString(currentLine, x, y);
    }
} 