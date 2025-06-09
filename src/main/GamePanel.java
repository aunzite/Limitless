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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

// Main game panel class that handles the game loop, rendering and updates
// Extends JPanel for GUI functionality and implements Runnable for the game loop
public class GamePanel extends JPanel implements Runnable, MouseListener, MouseMotionListener {

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
    public Saver saver;                                    // Handles save/load functionality
    public KeyHandler keyH;                                // Handles keyboard input
    private Thread gameThread;                              // Main game loop thread
    public CollisionChecker cCheck;                 // Handles collision detection
    public Player player;                           // Player entity
    public HUD hud;                                 // HUD object
    public Dialogue dialogue;                       // Dialogue system
    public AssetSetter aSetter;
    public SuperObject obj [];
    public NPC npc;                                 // NPC entity
    public Menu menu;                               // Main menu
    public OptionsMenu optionsMenu;                 // Options menu
    public PauseMenu pauseMenu;  // Add pause menu reference

    // Game state
    public static final int MENU_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int DIALOGUE_STATE = 2;
    public static final int OPTIONS_STATE = 3;
    public static final int PAUSE_STATE = 4;
    public int gameState = MENU_STATE;
    public boolean gamePaused = false;
    private boolean inDialogue = false;

    public JFrame frame;  // Changed to public
    private boolean isFullscreen = false;

    private float saveLoadAlpha = 0f;
    private static final float FADE_SPEED = 0.05f;

    // Add this field to GamePanel:
    private boolean canPickup = true;

    // Constructor: Initializes the game panel and sets up basic properties
    public GamePanel(JFrame frame) {
        this.frame = frame;
        
        // Set default screen dimensions
        screenWidth = 1536;
        screenHeight = 864;
        tileSize = screenWidth / 16; // Initial tile size
        
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        
        // Initialize components in correct order
        saver = new Saver(this);
        hud = new HUD(null);  // Create HUD first with null KeyHandler
        keyH = new KeyHandler(saver, hud);  // Create KeyHandler with HUD
        hud.setKeyHandler(keyH);  // Set KeyHandler in HUD
        
        // Load settings before initializing menus
        GameSettings.getInstance().loadSettings();
        
        menu = new Menu(this);
        optionsMenu = new OptionsMenu(this);
        pauseMenu = new PauseMenu(this);  // Initialize pause menu
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        // Initialize game objects
        tileM = new TileManager(this);
        cCheck = new CollisionChecker(this);
        player = new Player(this, keyH);
        player.weapon = null;
        npc = new NPC(this, keyH);
        dialogue = new Dialogue(this);
        aSetter = new AssetSetter(this);
        
        // Set initial positions
        player.worldX = (screenWidth / 2) + (tileSize * 5);  // Move 5 tiles right
        player.worldY = (screenHeight / 2) + (tileSize * 5); // Move 5 tiles down
        npc.worldX = (screenWidth / 2) + (tileSize * 15);    // Position NPC near water
        npc.worldY = (screenHeight / 2) + (tileSize * 7);    // Position NPC slightly below the water
        
        // Set frame to maximized
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Set initial game state to MENU_STATE
        gameState = MENU_STATE;
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
        aSetter.setObject();                    // Place objects in world
        // Add test items to inventory AFTER everything is initialized
        player.inventory.addItem(new OBJ_Apple());
        player.inventory.addItem(new OBJ_Apple());
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
        if (gameState == MENU_STATE) {
            menu.update();
            return;
        }
        
        if (gameState == OPTIONS_STATE) {
            optionsMenu.update();
            return;
        }
        
        if (gameState == PAUSE_STATE) {
            pauseMenu.update();
            return;
        }
        
        // Only update NPC if inventory is not open
        if (!player.inventory.isOpen()) {
            npc.update();
        }
        
        // Always update player
        if (gameState == PLAY_STATE) {
            player.update();
        }
        
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
        
        // Handle pause menu toggle
        if (keyH.escapePressed && gameState == PLAY_STATE) {
            gameState = PAUSE_STATE;
            keyH.escapePressed = false;
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
        
        // Always clear with full black
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        if (gameState == MENU_STATE) {
            menu.draw(g2);
            return;
        }
        
        if (gameState == OPTIONS_STATE) {
            optionsMenu.draw(g2);
            return;
        }
        
        if (gameState == PAUSE_STATE) {
            // Draw the game state first
            drawGameState(g2, xOffset, yOffset);
            // Then draw the pause menu on top
            pauseMenu.draw(g2);
            return;
        }
        
        // Draw the game state
        drawGameState(g2, xOffset, yOffset);
    }
    
    // Helper method to draw the game state
    private void drawGameState(Graphics2D g2, int xOffset, int yOffset) {
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
        
        // Draw objects and check for nearby items
        String nearbyItemDesc = null;
        boolean nearApple = false, nearSolthorn = false;
        int solthornScreenX = -1, solthornScreenY = -1, solthornObjIndex = -1;
        int solthornQuantity = 1; // For future-proofing, but Solthorn is unique
        for(int i = 0; i < obj.length; i++) {
            if(obj[i] != null) {
                obj[i].draw(g2, this);
                // Check if player is near this item
                int distance = (int) Math.sqrt(
                    Math.pow(obj[i].worldX - player.worldX, 2) + 
                    Math.pow(obj[i].worldY - player.worldY, 2)
                );
                if (distance < tileSize * 2) {
                    if (obj[i] instanceof object.OBJ_Apple) {
                        nearbyItemDesc = ((object.OBJ_Apple)obj[i]).getDescription();
                        nearApple = true;
                    } else if (obj[i] instanceof object.OBJ_Solthorn) {
                        nearbyItemDesc = ((object.OBJ_Solthorn)obj[i]).getDescription();
                        nearSolthorn = true;
                        solthornScreenX = obj[i].worldX - player.worldX + player.screenX;
                        solthornScreenY = obj[i].worldY - player.worldY + player.screenY;
                        solthornObjIndex = i;
                    }
                }
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
        
        int appleScreenX = -1, appleScreenY = -1, appleObjIndex = -1, appleQuantity = 1;
        if (nearApple) {
            // Find the apple's screen position and index
            for(int i = 0; i < obj.length; i++) {
                if(obj[i] != null && obj[i] instanceof object.OBJ_Apple) {
                    int distance = (int) Math.sqrt(
                        Math.pow(obj[i].worldX - player.worldX, 2) + 
                        Math.pow(obj[i].worldY - player.worldY, 2)
                    );
                    if (distance < tileSize * 2) {
                        appleScreenX = obj[i].worldX - player.worldX + player.screenX;
                        appleScreenY = obj[i].worldY - player.worldY + player.screenY;
                        appleObjIndex = i;
                        appleQuantity = ((object.OBJ_Apple)obj[i]).quantity;
                        break;
                    }
                }
            }
        }
        if (nearApple && appleScreenX != -1 && appleScreenY != -1) {
            int w = 340;
            int h = 160;
            int size = tileSize / 2;
            int x = appleScreenX + size + 32;
            int y = appleScreenY - h + 10;
            if (x + w > screenWidth) x = screenWidth - w - 10;
            if (y < 10) y = 10;
            if (y + h > screenHeight) y = screenHeight - h - 10;
            String name = "Apple";
            String[] lines = {"A fresh, juicy apple that restores your vitality.", "Effect: Restores 20 health and 15 stamina."};
            entity.Inventory.drawDetailsPopupBox(g2, x, y, w, h, name, lines, appleQuantity);
            float alpha = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 400.0));
            g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 16f));
            g2.setColor(new Color(255,255,255,(int)(220*alpha)));
            String pickupMsg = "Press e to pick up";
            int msgWidth = g2.getFontMetrics().stringWidth(pickupMsg);
            int msgX = x + (w - msgWidth) / 2;
            int msgY = y + h - 18;
            g2.drawString(pickupMsg, msgX, msgY);
            if (keyH.ePressed && canPickup && appleObjIndex != -1) {
                int qty = ((object.OBJ_Apple)obj[appleObjIndex]).quantity;
                player.inventory.addItem(new entity.OBJ_Apple(qty));
                obj[appleObjIndex] = null;
                canPickup = false;
            }
            if (!keyH.ePressed) {
                canPickup = true;
            }
        } else if (nearSolthorn && solthornScreenX != -1 && solthornScreenY != -1) {
            int w = 340;
            int h = 160;
            int size = tileSize / 2;
            int x = solthornScreenX + size + 32;
            int y = solthornScreenY - h + 10;
            if (x + w > screenWidth) x = screenWidth - w - 10;
            if (y < 10) y = 10;
            if (y + h > screenHeight) y = screenHeight - h - 10;
            String name = "Solthorn";
            String[] lines = {"A legendary blade passed down through",
                "Elaria's bloodline, forged around a gem said",
                "to hold unimaginable power."};
            entity.Inventory.drawDetailsPopupBox(g2, x, y, w, h, name, lines, solthornQuantity);
            float alpha = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 400.0));
            g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 16f));
            g2.setColor(new Color(255,255,255,(int)(220*alpha)));
            String pickupMsg = "Press e to pick up";
            int msgWidth = g2.getFontMetrics().stringWidth(pickupMsg);
            int msgX = x + (w - msgWidth) / 2;
            int msgY = y + h - 18;
            g2.drawString(pickupMsg, msgX, msgY);
            if (keyH.ePressed && canPickup && solthornObjIndex != -1) {
                player.inventory.addItem(new entity.Item("Solthorn", "res/object/solthorn.png", 1));
                obj[solthornObjIndex] = null;
                canPickup = false;
            }
            if (!keyH.ePressed) {
                canPickup = true;
            }
        }
        
        // Draw inventory overlay and inventory/items on top of everything
        if (player.inventory.isOpen()) {
            g2.setColor(new Color(0, 0, 0, 128));
            g2.fillRect(-xOffset, -yOffset, screenWidth, screenHeight);
            player.inventory.draw(g2);
        }
        
        // At the end, reset translation if needed
        g2.translate(-xOffset, -yOffset);
    }

    // Draw save/load/delete instructions
    private void drawSaveLoadInstructions(Graphics2D g2) {
        boolean isMoving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;
        
        // Update fade effect
        if (!isMoving && saveLoadAlpha < 1f) {
            saveLoadAlpha = Math.min(1f, saveLoadAlpha + FADE_SPEED);
        } else if (isMoving && saveLoadAlpha > 0f) {
            saveLoadAlpha = Math.max(0f, saveLoadAlpha - FADE_SPEED);
        }

        // Only draw if there's any alpha
        if (saveLoadAlpha > 0) {
            try {
                // Load the correct button images based on pressed state
                BufferedImage f5Sprite = ImageIO.read(new File(keyH.savePressed ? "res/buttons/f5p.png" : "res/buttons/f5o.png"));
                BufferedImage f6Sprite = ImageIO.read(new File(keyH.loadPressed ? "res/buttons/f6p.png" : "res/buttons/f6o.png"));
                BufferedImage f7Sprite = ImageIO.read(new File(keyH.deletePressed ? "res/buttons/f7p.png" : "res/buttons/f7o.png"));
                
                // Set composite for fade effect
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, saveLoadAlpha));
                
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

                // Reset composite
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (player.inventory.isOpen()) {
            boolean isRightClick = javax.swing.SwingUtilities.isRightMouseButton(e);
            player.inventory.handleMousePress(e.getX(), e.getY(), isRightClick);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (player.inventory.isOpen()) {
            player.inventory.handleMouseDrag(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (player.inventory.isOpen()) {
            player.inventory.handleMouseRelease(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
}