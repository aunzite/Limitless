package main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class OptionsMenu implements MouseListener, MouseMotionListener {
    private GamePanel gp;
    private GameSettings settings;
    private int selectedOption = 0;
    private int hoveredOption = -1;
    private float[] optionScales;
    private static final float SCALE_SPEED = 0.1f;
    private static final float MAX_SCALE = 1.2f;
    
    // UI elements
    private Rectangle backButton;
    private Rectangle autoSaveToggle;
    private BufferedImage backgroundImage;
    
    // Fonts
    private Font titleFont;
    private Font menuFont;
    
    private GifImage backgroundGif;
    
    public OptionsMenu(GamePanel gp) {
        this.gp = gp;
        this.settings = GameSettings.getInstance();
        
        // Initialize fonts
        titleFont = new Font("Arial", Font.BOLD, 40);
        menuFont = new Font("Arial", Font.PLAIN, 30);
        
        // Initialize option scales
        optionScales = new float[1]; // Auto-save
        for (int i = 0; i < optionScales.length; i++) {
            optionScales[i] = 1.0f;
        }
        
        // Initialize auto-save toggle
        int toggleWidth = 100;
        int toggleHeight = 50;
        int toggleX = gp.screenWidth / 2 - toggleWidth / 2;
        int toggleY = gp.screenHeight / 2;
        autoSaveToggle = new Rectangle(toggleX, toggleY, toggleWidth, toggleHeight);
        
        // Initialize back button
        backButton = new Rectangle(gp.screenWidth / 2 - 100, gp.screenHeight - 100, 200, 50);
        
        // Add mouse listeners
        gp.addMouseListener(this);
        gp.addMouseMotionListener(this);
        
        // Load background GIF (same as Menu)
        try {
            backgroundGif = new GifImage("res/menu/menu.gif");
        } catch (Exception e) {
            System.err.println("Error loading background GIF in OptionsMenu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void update() {
        // Update scales for hover effect
        for (int i = 0; i < optionScales.length; i++) {
            if (i == hoveredOption) {
                optionScales[i] = Math.min(MAX_SCALE, optionScales[i] + SCALE_SPEED);
            } else {
                optionScales[i] = Math.max(1.0f, optionScales[i] - SCALE_SPEED);
            }
        }
        
        // Handle escape key to go back
        if (gp.keyH.escapePressed) {
            gp.gameState = GamePanel.MENU_STATE;
            gp.keyH.escapePressed = false;
        }
    }
    
    public void draw(Graphics2D g2) {
        // Draw background GIF
        if (backgroundGif != null) {
            BufferedImage currentFrame = backgroundGif.getCurrentFrame();
            if (currentFrame != null) {
                // Scale to fit the screen
                double scaleX = (double) gp.screenWidth / currentFrame.getWidth();
                double scaleY = (double) gp.screenHeight / currentFrame.getHeight();
                double scale = Math.max(scaleX, scaleY);
                int scaledWidth = (int) (currentFrame.getWidth() * scale);
                int scaledHeight = (int) (currentFrame.getHeight() * scale);
                int x = (gp.screenWidth - scaledWidth) / 2;
                int y = (gp.screenHeight - scaledHeight) / 2;
                g2.drawImage(currentFrame, x, y, scaledWidth, scaledHeight, null);
            }
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
        // Draw a darker semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 180)); // More opaque than menu
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Draw title
        g2.setFont(titleFont);
        String title = "OPTIONS";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (gp.screenWidth - fm.stringWidth(title)) / 2;
        int titleY = gp.screenHeight / 6;
        
        // Draw title with shadow
        g2.setColor(Color.BLACK);
        g2.drawString(title, titleX + 4, titleY + 4);
        g2.setColor(Color.WHITE);
        g2.drawString(title, titleX, titleY);
        
        // Draw options
        g2.setFont(menuFont);
        fm = g2.getFontMetrics();
        
        // Draw auto-save toggle
        drawAutoSaveToggle(g2);
        
        // Draw back button
        drawBackButton(g2);
    }
    
    private void drawAutoSaveToggle(Graphics2D g2) {
        String label = "Auto Save";
        FontMetrics fm = g2.getFontMetrics();
        int labelX = gp.screenWidth / 4;
        int labelY = gp.screenHeight / 2 + fm.getAscent() / 2;
        
        // Draw label
        g2.setColor(Color.WHITE);
        g2.drawString(label, labelX, labelY);
        
        // Draw toggle button
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(autoSaveToggle.x, autoSaveToggle.y, autoSaveToggle.width, autoSaveToggle.height);
        
        // Draw toggle state
        String state = settings.isAutoSaveEnabled() ? "ON" : "OFF";
        g2.setColor(Color.WHITE);
        int stateX = autoSaveToggle.x + (autoSaveToggle.width - fm.stringWidth(state)) / 2;
        int stateY = autoSaveToggle.y + (autoSaveToggle.height + fm.getAscent()) / 2;
        g2.drawString(state, stateX, stateY);
    }
    
    private void drawBackButton(Graphics2D g2) {
        String label = "Back";
        FontMetrics fm = g2.getFontMetrics();
        
        // Draw button background
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(backButton.x, backButton.y, backButton.width, backButton.height);
        
        // Draw text
        g2.setColor(Color.WHITE);
        int textX = backButton.x + (backButton.width - fm.stringWidth(label)) / 2;
        int textY = backButton.y + (backButton.height + fm.getAscent()) / 2;
        g2.drawString(label, textX, textY);
    }
    
    // MouseListener methods
    @Override
    public void mouseClicked(MouseEvent e) {
        Point mousePoint = e.getPoint();
        
        // Check back button
        if (backButton.contains(mousePoint)) {
            gp.gameState = GamePanel.MENU_STATE;
            return;
        }
        
        // Check auto-save toggle
        if (autoSaveToggle.contains(mousePoint)) {
            settings.setAutoSaveEnabled(!settings.isAutoSaveEnabled());
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {
        hoveredOption = -1;
    }
    
    // MouseMotionListener methods
    @Override
    public void mouseMoved(MouseEvent e) {
        // Check which option is being hovered
        if (autoSaveToggle.contains(e.getPoint())) {
            hoveredOption = 0;
        } else {
            hoveredOption = -1;
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {}
} 