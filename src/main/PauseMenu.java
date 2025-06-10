package main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;

public class PauseMenu implements MouseListener, MouseMotionListener {
    private GamePanel gp;
    private int selectedOption = 0;
    private final String[] options = {"Resume", "Main Menu", "Quit"};
    private Font titleFont;
    private Font menuFont;
    private Rectangle[] optionBounds;
    private int hoveredOption = -1;
    private float[] optionScales;
    private static final float SCALE_SPEED = 0.1f;
    private static final float MAX_SCALE = 1.2f;
    private AudioManager audioManager;
    
    public PauseMenu(GamePanel gp) {
        this.gp = gp;
        this.audioManager = AudioManager.getInstance();
        
        // Initialize fonts
        titleFont = new Font("Arial", Font.BOLD, 60);
        menuFont = new Font("Arial", Font.BOLD, 30);
        
        // Initialize menu options
        optionBounds = new Rectangle[options.length];
        optionScales = new float[options.length];
        for (int i = 0; i < options.length; i++) {
            optionScales[i] = 1.0f;
            // Initialize option bounds with default values
            optionBounds[i] = new Rectangle(0, 0, 0, 0);
        }
        
        // Add mouse listeners
        gp.addMouseListener(this);
        gp.addMouseMotionListener(this);
    }
    
    public void update() {
        // Update scales for hover effect
        for (int i = 0; i < options.length; i++) {
            if (i == hoveredOption) {
                optionScales[i] = Math.min(MAX_SCALE, optionScales[i] + SCALE_SPEED);
            } else {
                optionScales[i] = Math.max(1.0f, optionScales[i] - SCALE_SPEED);
            }
        }
        
        // Keyboard navigation
        if (gp.keyH.upPressed) {
            selectedOption = (selectedOption - 1 + options.length) % options.length;
            gp.keyH.upPressed = false;
        }
        if (gp.keyH.downPressed) {
            selectedOption = (selectedOption + 1) % options.length;
            gp.keyH.downPressed = false;
        }
        if (gp.keyH.enterPressed) {
            handleSelection(selectedOption);
            gp.keyH.enterPressed = false;
        }
        
        // Handle escape key to resume
        if (gp.keyH.escapePressed) {
            gp.gameState = GamePanel.PLAY_STATE;
            gp.keyH.escapePressed = false;
        }
    }
    
    private void handleSelection(int option) {
        switch (option) {
            case 0: // Resume
                gp.gameState = GamePanel.PLAY_STATE;
                break;
            case 1: // Main Menu
                audioManager.stopMusic(); // Stop current music
                audioManager.playMainMenuMusic(); // Play menu music
                gp.gameState = GamePanel.MENU_STATE;
                break;
            case 2: // Quit
                System.exit(0);
                break;
        }
    }
    
    public void draw(Graphics2D g2) {
        // Draw semi-transparent black overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Draw title
        g2.setFont(titleFont);
        String title = "PAUSED";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (gp.screenWidth - fm.stringWidth(title)) / 2;
        int titleY = gp.screenHeight / 4;
        
        // Draw title with shadow
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(title, titleX + 4, titleY + 4);
        g2.setColor(Color.WHITE);
        g2.drawString(title, titleX, titleY);
        
        // Draw menu options
        g2.setFont(menuFont);
        fm = g2.getFontMetrics();
        int optionY = gp.screenHeight / 2;
        int optionSpacing = 60;
        
        for (int i = 0; i < options.length; i++) {
            // Calculate option position
            int optionX = (gp.screenWidth - fm.stringWidth(options[i])) / 2;
            
            // Update option bounds for mouse interaction
            optionBounds[i] = new Rectangle(
                optionX - 20,
                optionY - fm.getHeight() + 5,
                fm.stringWidth(options[i]) + 40,
                fm.getHeight() + 10
            );
            
            // Apply scale transformation
            AffineTransform oldTransform = g2.getTransform();
            g2.translate(optionX + fm.stringWidth(options[i]) / 2, optionY);
            g2.scale(optionScales[i], optionScales[i]);
            g2.translate(-(optionX + fm.stringWidth(options[i]) / 2), -optionY);
            
            // Draw option text with shadow
            g2.setColor(new Color(0, 0, 0, 150));
            g2.drawString(options[i], optionX + 4, optionY + 4);
            g2.setColor(Color.WHITE);
            g2.drawString(options[i], optionX, optionY);
            
            // Restore original transform
            g2.setTransform(oldTransform);
            
            optionY += optionSpacing;
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gp.gameState != GamePanel.PAUSE_STATE) return;
        
        Point mousePoint = e.getPoint();
        for (int i = 0; i < options.length; i++) {
            if (optionBounds[i].contains(mousePoint)) {
                handleSelection(i);
                break;
            }
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
        if (gp.gameState != GamePanel.PAUSE_STATE) return;
        hoveredOption = -1;
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        if (gp.gameState != GamePanel.PAUSE_STATE) return;
        
        Point mousePoint = e.getPoint();
        for (int i = 0; i < options.length; i++) {
            if (optionBounds[i].contains(mousePoint)) {
                hoveredOption = i;
                return;
            }
        }
        hoveredOption = -1;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {}
} 