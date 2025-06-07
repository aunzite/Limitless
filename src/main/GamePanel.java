/////////////////////////////////////////////////////////////////////////////
// Limitless
// GamePanel.java
// 
// Description: Main game panel that handles core game functionality. This class:
// - Manages the game loop and timing system (Aun)
// - Handles screen and world settings (Aun)
// - Controls rendering pipeline and layers (Aun)
// - Coordinates player and tile updates (Aun)
// - Manages input processing and game state (Aun)
/////////////////////////////////////////////////////////////////////////////

package main;

import entity.*;
import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import object.SuperObject;
import tile.TileManager;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Main game panel class that handles the game loop, rendering and updates
// Extends JPanel for GUI functionality and implements Runnable for the game loop
public class GamePanel extends JPanel implements Runnable {

    // Screen settings
    public int screenWidth;
    public int screenHeight;
    public int tileSize;
    
    // World Settings
    public final int maxWorldCol = 69;                       // Total number of columns in world map
    public final int maxWorldRow = 39;                       // Total number of rows in world map
    public final int worldWidth = tileSize * maxWorldCol;    // Total world width in pixels
    public final int worldHeight = tileSize * maxWorldRow;   // Total world height in pixels

    // Game Components
    private int FPS = 60;                     // Target frames per second
    public TileManager tileM;                              // Manages the game's tiles/map
    private Saver saver;                                    // Handles save/load functionality
    private KeyHandler keyH;                                // Handles keyboard input
    private Thread gameThread;                              // Main game loop thread
    public CollisionChecker cCheck;                 // Handles collision detection
    public Player player;                           // Player entity
    public HUD hud;                                 // HUD object
    public Dialogue dialogue;                       // Dialogue system
    public AssetSetter aSetter;
    public SuperObject obj [];
    public NPC npc;                                 // NPC entity

    // Game state
    private boolean gamePaused = false;
    private boolean inDialogue = false;

    public JFrame frame;  // Changed to public
    private boolean isFullscreen = false;

    // Constructor: Initializes the game panel and sets up basic properties
    public GamePanel(JFrame frame) {
        this.frame = frame;
        
        // Set default screen dimensions
        screenWidth = 1536;
        screenHeight = 864;
        tileSize = screenWidth / 16; // Initial tile size
        
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        
        // Initialize components in correct order
        saver = new Saver(this);
        hud = new HUD();
        keyH = new KeyHandler(saver, hud);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        // Initialize game objects
        tileM = new TileManager(this);
        cCheck = new CollisionChecker(this);
        player = new Player(this, keyH);
        npc = new NPC(this, keyH);
        dialogue = new Dialogue(this);
        
        // Set initial positions
        player.worldX = (screenWidth / 2) + (tileSize * 5);  // Move 5 tiles right
        player.worldY = (screenHeight / 2) + (tileSize * 5); // Move 5 tiles down
        npc.worldX = (screenWidth / 2) + (tileSize * 15);    // Position NPC near water
        npc.worldY = (screenHeight / 2) + (tileSize * 7);    // Position NPC slightly below the water
        
        // Set frame to maximized
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void toggleFullscreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        if (!isFullscreen) {
            // Enter fullscreen
            gd.setFullScreenWindow(frame);
            isFullscreen = true;
        } else {
            // Exit fullscreen
            gd.setFullScreenWindow(null);
            isFullscreen = false;
        }
        
        // Update screen dimensions
        screenWidth = frame.getWidth();
        screenHeight = frame.getHeight();
        
        // Update tile size based on screen dimensions
        tileSize = screenWidth / 16; // Adjust this ratio as needed
        
        // Update player and NPC positions
        player.worldX = screenWidth / 2;
        player.worldY = screenHeight / 2;
        npc.worldX = screenWidth / 2 - 100;
        npc.worldY = screenHeight / 2;
    }

    public void setupGame() {
        obj = new SuperObject[10];              // Initialize object array
        aSetter = new AssetSetter(this);        // Create asset setter
        aSetter.setObject();                    // Place objects in world
    }

    // Starts the game thread and begins the game loop
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    // Main game loop implementation
    // Handles timing, updates, and rendering at a fixed rate (60 FPS)
    public void run() {
        double drawInterval = 1000000000/FPS;  // Time per frame in nanoseconds
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        // Game loop
        while(gameThread != null) {
            currentTime = System.nanoTime();
            
            // Accumulate time since last update
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            // Update and render when enough time has passed
            if(delta >= 1) {
                update();    // Update game state
                repaint();   // Trigger paintComponent
                delta--;     // Reset time accumulator
                drawCount++;
            }
            
        }
    }

    // Updates game state (called every frame)
    public void update() {
        player.update();    // Update player position and state
        npc.update();       // Update NPC state
        
        // Handle fullscreen toggle
        if (keyH.f11Pressed) {
            toggleFullscreen();
            keyH.f11Pressed = false;
        }
        
        // Handle dialogue clearing
        if (keyH.enterPressed && !dialogue.getLine().equals("")) {
            dialogue.clear();
            keyH.enterPressed = false; // Prevent multiple triggers
        }
    }

    // Renders the game (called every frame)
    // Order of drawing determines layer visibility
    @Override
    public void paintComponent(Graphics g) {
        // Update dynamic values
        screenWidth = getWidth();
        screenHeight = getHeight();
        // Calculate tileSize to preserve 16:9 aspect ratio and keep tiles square
        int tileSizeW = screenWidth / 16;
        int tileSizeH = screenHeight / 9;
        tileSize = Math.min(tileSizeW, tileSizeH);
        int gameAreaWidth = tileSize * 16;
        int gameAreaHeight = tileSize * 9;
        int xOffset = (screenWidth - gameAreaWidth) / 2;
        int yOffset = (screenHeight - gameAreaHeight) / 2;
        
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        // Draw black bars (letterboxing/pillarboxing)
        g2.setColor(Color.BLACK);
        if (xOffset > 0) {
            g2.fillRect(0, 0, xOffset, screenHeight); // Left bar
            g2.fillRect(screenWidth - xOffset, 0, xOffset, screenHeight); // Right bar
        }
        if (yOffset > 0) {
            g2.fillRect(0, 0, screenWidth, yOffset); // Top bar
            g2.fillRect(0, screenHeight - yOffset, screenWidth, yOffset); // Bottom bar
        }
        // Translate graphics context to game area
        g2.translate(xOffset, yOffset);
        
        // Draw tiles
        tileM.draw(g2);
        
        // Draw objects
        for(int i = 0; i < obj.length; i++) {
            if(obj[i] != null) {
                obj[i].draw(g2, this);
            }
        }
        
        // Draw NPC
        npc.draw(g2);
        
        // Draw player
        player.draw(g2);
        
        // Draw HUD
        hud.draw(g2, player.weapon);
        
        // Draw save/load/delete instructions
        drawSaveLoadInstructions(g2);
        
        // Draw dialogue if active
        if(!dialogue.getLine().equals("")) {
            dialogue.draw(g2);
        }
        
        // At the end, reset translation if needed
        g2.translate(-xOffset, -yOffset);
        g2.dispose();
    }

    // Draw save/load/delete instructions
    private void drawSaveLoadInstructions(Graphics2D g2) {
        if (!keyH.upPressed && !keyH.downPressed && !keyH.leftPressed && !keyH.rightPressed) {
            try {
                // Load the instruction images
                BufferedImage f5Sprite = ImageIO.read(new File("res/buttons/f5.png"));
                BufferedImage f6Sprite = ImageIO.read(new File("res/buttons/f6.png"));
                BufferedImage f7Sprite = ImageIO.read(new File("res/buttons/f7.png"));
                
                // Set black color for text
                g2.setColor(new Color(0, 0, 0, 200));
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                
                // Draw sprites in top right corner
                int x = screenWidth - 60;  // Moved even more to the right
                int y = 20;
                int spriteSize = 32;
                int verticalSpacing = 50;
                
                // Draw F5 sprite and text
                g2.drawImage(f5Sprite, x, y, spriteSize, spriteSize, null);
                String f5Text = "Save";
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(f5Text);
                g2.drawString(f5Text, x - textWidth - 10, y + spriteSize/2 + 5);
                
                // Draw F6 sprite and text
                g2.drawImage(f6Sprite, x, y + verticalSpacing, spriteSize, spriteSize, null);
                String f6Text = "Load";
                textWidth = fm.stringWidth(f6Text);
                g2.drawString(f6Text, x - textWidth - 10, y + verticalSpacing + spriteSize/2 + 5);
                
                // Draw F7 sprite and text
                g2.drawImage(f7Sprite, x, y + verticalSpacing * 2, spriteSize, spriteSize, null);
                String f7Text = "Delete";
                textWidth = fm.stringWidth(f7Text);
                g2.drawString(f7Text, x - textWidth - 10, y + verticalSpacing * 2 + spriteSize/2 + 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}