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
    private Rectangle[] brightnessSliders;
    private Rectangle[] contrastSliders;
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
        optionScales = new float[2]; // Brightness, Contrast
        for (int i = 0; i < optionScales.length; i++) {
            optionScales[i] = 1.0f;
        }
        
        // Initialize slider arrays
        brightnessSliders = new Rectangle[1];
        contrastSliders = new Rectangle[1];
        
        // Create slider rectangles
        int sliderWidth = 200;
        int sliderHeight = 20;
        int sliderX = gp.screenWidth / 2 - sliderWidth / 2;
        int brightnessY = gp.screenHeight / 2;
        int contrastY = brightnessY + 100;
        
        brightnessSliders[0] = new Rectangle(sliderX, brightnessY, sliderWidth, sliderHeight);
        contrastSliders[0] = new Rectangle(sliderX, contrastY, sliderWidth, sliderHeight);
        
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
        int startY = gp.screenHeight / 3;
        int spacing = 80;
        
        // Draw brightness slider
        drawBrightnessSlider(g2, startY);
        
        // Draw contrast slider
        drawContrastSlider(g2, startY + spacing);
        
        // Draw back button
        drawBackButton(g2);
    }
    
    private void drawBrightnessSlider(Graphics2D g2, int y) {
        String label = "Brightness";
        FontMetrics fm = g2.getFontMetrics();
        int labelX = gp.screenWidth / 4;
        
        // Draw label
        g2.setColor(Color.WHITE);
        g2.drawString(label, labelX, y);
        
        // Draw slider
        int sliderX = gp.screenWidth / 2;
        int sliderWidth = 200;
        int sliderHeight = 20;
        
        // Draw slider background
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(sliderX, y - sliderHeight, sliderWidth, sliderHeight);
        
        // Draw slider handle
        int handleX = sliderX + (int)(settings.getBrightness() * sliderWidth / 2.0f);
        g2.setColor(Color.WHITE);
        g2.fillRect(handleX - 5, y - sliderHeight - 5, 10, sliderHeight + 10);
        
        // Store slider bounds for mouse interaction
        brightnessSliders[0] = new Rectangle(sliderX, y - sliderHeight, sliderWidth, sliderHeight);
    }
    
    private void drawContrastSlider(Graphics2D g2, int y) {
        String label = "Contrast";
        FontMetrics fm = g2.getFontMetrics();
        int labelX = gp.screenWidth / 4;
        
        // Draw label
        g2.setColor(Color.WHITE);
        g2.drawString(label, labelX, y);
        
        // Draw slider
        int sliderX = gp.screenWidth / 2;
        int sliderWidth = 200;
        int sliderHeight = 20;
        
        // Draw slider background
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(sliderX, y - sliderHeight, sliderWidth, sliderHeight);
        
        // Draw slider handle
        int handleX = sliderX + (int)(settings.getContrast() * sliderWidth / 2.0f);
        g2.setColor(Color.WHITE);
        g2.fillRect(handleX - 5, y - sliderHeight - 5, 10, sliderHeight + 10);
        
        // Store slider bounds for mouse interaction
        contrastSliders[0] = new Rectangle(sliderX, y - sliderHeight, sliderWidth, sliderHeight);
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
        
        // Check brightness slider
        if (brightnessSliders[0].contains(mousePoint)) {
            float value = (float)(mousePoint.x - brightnessSliders[0].x) / brightnessSliders[0].width;
            value = Math.max(0.0f, Math.min(1.0f, value));
            settings.setBrightness(value * 2.0f);
        }
        
        // Check contrast slider
        if (contrastSliders[0].contains(mousePoint)) {
            float value = (float)(mousePoint.x - contrastSliders[0].x) / contrastSliders[0].width;
            value = Math.max(0.0f, Math.min(1.0f, value));
            settings.setContrast(value * 2.0f);
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
        if (brightnessSliders[0].contains(e.getPoint())) {
            hoveredOption = 0;
        } else if (contrastSliders[0].contains(e.getPoint())) {
            hoveredOption = 1;
        } else {
            hoveredOption = -1;
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        Point mousePoint = e.getPoint();
        
        // Handle brightness slider drag
        if (brightnessSliders[0].contains(mousePoint)) {
            float value = (float)(mousePoint.x - brightnessSliders[0].x) / brightnessSliders[0].width;
            value = Math.max(0.0f, Math.min(1.0f, value));
            settings.setBrightness(value * 2.0f);
        }
        
        // Handle contrast slider drag
        if (contrastSliders[0].contains(mousePoint)) {
            float value = (float)(mousePoint.x - contrastSliders[0].x) / contrastSliders[0].width;
            value = Math.max(0.0f, Math.min(1.0f, value));
            settings.setContrast(value * 2.0f);
        }
    }
} 