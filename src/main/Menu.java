package main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Menu implements MouseListener, MouseMotionListener {
    private GamePanel gp;
    private final String[] options = {"Play", "Options", "Quit"};
    private Font titleFont = new Font("Comic Sans MS", Font.BOLD, 72);
    private Font menuFont = new Font("Comic Sans MS", Font.PLAIN, 36);
    private Rectangle[] optionBounds;
    private int hoveredOption = -1;
    private float[] optionScales;
    private static final float SCALE_SPEED = 0.1f;
    private static final float MAX_SCALE = 1.2f;
    private GifImage backgroundGif;
    private BufferedImage frameBuffer;
    private int frameWidth;
    private int frameHeight;
    private AudioManager audioManager;
    
    public Menu(GamePanel gp) {
        this.gp = gp;
        this.audioManager = AudioManager.getInstance();
        
        // Initialize fonts
        titleFont = new Font("Comic Sans MS", Font.BOLD, 72);
        menuFont = new Font("Comic Sans MS", Font.PLAIN, 36);
        
        // Initialize menu options
        optionBounds = new Rectangle[options.length];
        optionScales = new float[options.length];
        for (int i = 0; i < options.length; i++) {
            optionScales[i] = 1.0f;
            // Initialize option bounds with default values
            optionBounds[i] = new Rectangle(0, 0, 0, 0);
        }
        
        // Load background GIF
        try {
            backgroundGif = new GifImage("res/menu/menu.gif");
            // Create a fixed-size buffer for the animation
            if (backgroundGif != null) {
                BufferedImage firstFrame = backgroundGif.getCurrentFrame();
                if (firstFrame != null) {
                    frameWidth = gp.screenWidth;
                    frameHeight = gp.screenHeight;
                    frameBuffer = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading background GIF: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Add mouse listeners
        gp.addMouseListener(this);
        gp.addMouseMotionListener(this);
        
        // Start playing main menu music
        audioManager.playMainMenuMusic();
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
            hoveredOption = (hoveredOption - 1 + options.length) % options.length;
            gp.keyH.upPressed = false;
        }
        if (gp.keyH.downPressed) {
            hoveredOption = (hoveredOption + 1) % options.length;
            gp.keyH.downPressed = false;
        }
        if (gp.keyH.enterPressed) {
            handleSelection(hoveredOption);
            gp.keyH.enterPressed = false;
        }
    }
    
    private void handleSelection(int option) {
        switch (option) {
            case 0: // Play
                audioManager.stopMusic(); // Stop menu music when starting game
                audioManager.playMainAreaMusic(); // Play main area music
                gp.gameState = GamePanel.PLAY_STATE;
                break;
            case 1: // Options
                gp.gameState = GamePanel.OPTIONS_STATE;
                break;
            case 2: // Quit
                System.exit(0);
                break;
        }
    }
    
    public void draw(Graphics2D g2) {
        // Draw background GIF
        if (backgroundGif != null && frameBuffer != null) {
            BufferedImage currentFrame = backgroundGif.getCurrentFrame();
            if (currentFrame != null) {
                // Clear the buffer
                Graphics2D bufferG2 = frameBuffer.createGraphics();
                bufferG2.setColor(new Color(0, 0, 0, 0));
                bufferG2.fillRect(0, 0, frameWidth, frameHeight);
                
                // Draw the current frame scaled to fit the buffer
                bufferG2.drawImage(currentFrame, 0, 0, frameWidth, frameHeight, null);
                bufferG2.dispose();
                
                // Draw the buffer to the screen
                g2.drawImage(frameBuffer, 0, 0, null);
            }
        } else {
            // If no GIF is loaded, draw a black background
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
        
        // Draw semi-transparent overlay with reduced opacity
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Draw title
        g2.setFont(titleFont);
        String title = "LIMITLESS";
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
        
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            int optionWidth = fm.stringWidth(option);
            int optionX = (gp.screenWidth - optionWidth) / 2;
            
            // Update option bounds for mouse interaction
            optionBounds[i] = new Rectangle(optionX - 10, optionY - fm.getHeight(), 
                                          optionWidth + 20, fm.getHeight() + 10);
            
            // Save the current transform
            AffineTransform oldTransform = g2.getTransform();
            
            // Apply scaling transformation
            float scale = optionScales[i];
            g2.translate(optionX + optionWidth/2, optionY);
            g2.scale(scale, scale);
            g2.translate(-(optionX + optionWidth/2), -optionY);
            
            // Draw option with shadow
            g2.setColor(new Color(0, 0, 0, 150));
            g2.drawString(option, optionX + 2, optionY + 2);
            
            // Always use white color for options
            g2.setColor(Color.WHITE);
            g2.drawString(option, optionX, optionY);
            
            // Restore the original transform
            g2.setTransform(oldTransform);
            
            optionY += 60;
        }
    }
    
    // MouseListener methods
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gp.gameState != GamePanel.MENU_STATE) return;
        
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
        if (gp.gameState != GamePanel.MENU_STATE) return;
        hoveredOption = -1;
    }
    
    // MouseMotionListener methods
    @Override
    public void mouseMoved(MouseEvent e) {
        if (gp.gameState != GamePanel.MENU_STATE) return;
        
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