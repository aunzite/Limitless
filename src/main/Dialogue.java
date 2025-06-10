package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Dialogue {
    private String line = "";
    private GamePanel gp;
    private float alpha = 0.0f;
    private boolean fadeIn = true;
    
    public Dialogue(GamePanel gp) {
        this.gp = gp;
    }
    
    public void setLine(String line) {
        this.line = line;
    }
    
    public String getLine() {
        return line;
    }
    
    public void clear() {
        line = "";
    }
    
    public void draw(Graphics2D g2) {
        // Draw dialogue box at the bottom of the screen
        int x = 0;
        int y = gp.screenHeight - 100; // Position at bottom with some padding
        int width = gp.screenWidth;
        int height = 100;
        
        // Draw semi-transparent black background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(x, y, width, height);
        
        // Draw white border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, width, height);
        
        // Draw text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        
        // Split text into multiple lines if needed
        String[] words = line.split(" ");
        String currentLine = "";
        int lineY = y + 30;
        
        for(String word : words) {
            if(g2.getFontMetrics().stringWidth(currentLine + " " + word) < width - 40) {
                currentLine += word + " ";
            } else {
                g2.drawString(currentLine, x + 20, lineY);
                currentLine = word + " ";
                lineY += 25;
            }
        }
        g2.drawString(currentLine, x + 20, lineY);
        
        // Draw "Press Enter to continue" with pulsing effect
        if(!line.equals("")) {
            int alpha = (int)(128 + 127 * Math.sin(System.currentTimeMillis() / 200.0));
            g2.setColor(new Color(255, 255, 255, alpha));
            g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 16));
            String continueText = "Press Enter to continue";
            int textWidth = g2.getFontMetrics().stringWidth(continueText);
            g2.drawString(continueText, x + width - textWidth - 20, y + height - 20);
        }
    }
} 