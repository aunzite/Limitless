/////////////////////////////////////////////////////////////////////////////
// Limitless
// Dialogue.java
// 
// Description: Manages in-game dialogue including:
// - NPC conversations
// - Story text
// - Tutorial messages
// - System notifications
/////////////////////////////////////////////////////////////////////////////

package main;

import java.awt.*;

// Handles all dialogue and text display in the game
public class Dialogue {
    // Game panel reference
    private GamePanel gp;
    
    // Dialogue state
    private String currentLine;      // Current dialogue line
    
    // Text display settings
    private static final int BOX_PADDING = 20;    // Padding around text
    private static final int LINE_SPACING = 25;   // Space between lines
    private static final int FONT_SIZE = 20;      // Base font size
    private static final int CONTINUE_FONT_SIZE = 16;  // Continue text font size
    
    // Constructor
    public Dialogue(GamePanel gp) {
        this.gp = gp;
        this.currentLine = "";
    }
    
    // Set the current dialogue line
    public void setLine(String line) {
        this.currentLine = line;
    }
    
    // Get the current dialogue line
    public String getLine() {
        return currentLine;
    }
    
    // Clear the current dialogue
    public void clear() {
        this.currentLine = "";
    }
    
    // Draw the dialogue box and text
    public void draw(Graphics2D g2) {
        if (currentLine.equals("")) return;
        
        // Calculate box position and size
        int x = 0;
        int y = gp.screenHeight - 100;
        int width = gp.screenWidth;
        int height = 100;
        
        // Draw semi-transparent black background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(x, y, width, height);
        
        // Draw white border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, width, height);
        
        // Set up text rendering
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, FONT_SIZE));
        
        // Split and draw text
        String[] words = currentLine.split(" ");
        String line = "";
        int lineY = y + 30;
        
        for (String word : words) {
            if (g2.getFontMetrics().stringWidth(line + " " + word) < width - BOX_PADDING * 2) {
                line += word + " ";
            } else {
                g2.drawString(line, x + BOX_PADDING, lineY);
                line = word + " ";
                lineY += LINE_SPACING;
            }
        }
        g2.drawString(line, x + BOX_PADDING, lineY);
        
        // Draw continue prompt with pulsing effect
        int alpha = (int)(128 + 127 * Math.sin(System.currentTimeMillis() / 200.0));
        g2.setColor(new Color(255, 255, 255, alpha));
        g2.setFont(new Font("Comic Sans MS", Font.ITALIC, CONTINUE_FONT_SIZE));
        String continueText = "Press Enter to continue";
        int textWidth = g2.getFontMetrics().stringWidth(continueText);
        g2.drawString(continueText, x + width - textWidth - BOX_PADDING, y + height - BOX_PADDING);
    }
} 