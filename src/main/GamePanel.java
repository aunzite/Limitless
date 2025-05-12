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
import tile.TileManager;

// Main game panel class that handles the game loop, rendering and updates
// Extends JPanel for GUI functionality and implements Runnable for the game loop
public class GamePanel extends JPanel implements Runnable {

    // Screen Settings
    final int originalTileSize = 16;                         // Original tile size (16x16 pixels)
    final int scale = 4;                                     // Scale factor for modern displays
    public final int tileSize = originalTileSize * scale;    // Actual tile size used in game (48x48)
    public final int maxScreenCol = 16;                      // Number of columns that fit on screen
    public final int maxScreenRow = 12;                      // Number of rows that fit on screen
    public final int screenWidth = tileSize * maxScreenCol;  // Total screen width in pixels
    public final int screenHeight = tileSize * maxScreenRow; // Total screen height in pixels

    // World Settings
    public final int maxWorldCol = 69;                       // Total number of columns in world map
    public final int maxWorldRow = 39;                       // Total number of rows in world map
    public final int worldWidth = tileSize * maxWorldCol;    // Total world width in pixels
    public final int worldHeight = tileSize * maxWorldRow;   // Total world height in pixels

    // Game Components
    private final int FPS = 60;                     // Target frames per second
    TileManager tileM;                              // Manages the game's tiles/map
    Saver saver;                                    // Handles save/load functionality
    KeyHandler keyH;                                // Handles keyboard input
    Thread gameThread;                              // Main game loop thread
    public CollisionChecker cCheck;                 // Handles collision detection
    public Player player;                           // Player entity
    public HUD hud;                                 // HUD object
    public Dialogue dialogue;                       // Dialogue system

    // Constructor: Initializes the game panel and sets up basic properties
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        
        // Initialize components in correct order
        saver = new Saver(this);                    // Initialize saver first
        keyH = new KeyHandler(saver);               // Pass saver to key handler
        tileM = new TileManager(this);              // Initialize tile manager
        cCheck = new CollisionChecker(this);        // Initialize collision checker
        player = new Player(this, keyH);            // Initialize player last
        hud = new HUD();                           // Initialize HUD
        dialogue = new Dialogue();                  // Initialize dialogue system
        
        this.addKeyListener(keyH);                  // Enable keyboard input
        this.setFocusable(true);                   // Allow panel to receive input focus
        
        dialogue.setLine("Welcome to the village!");
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

            // Display FPS counter every second
            if(timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                System.out.println("Remember, F5 is for saving, F6 is for loading, and F7 is for deleting your save file.");
                drawCount = 0;
                timer = 0;
            }
            
        }
    }

    // Updates game state (called every frame)
    public void update() {
        player.update();    // Update player position and state
        
        // (Ahmed) If Enter is pressed and there's dialogue, clear it
        if (keyH.enterPressed && !dialogue.getLine().equals("")) {
        dialogue.clear();
        keyH.enterPressed = false; // Prevent multiple triggers
        }
    }

    // Renders the game (called every frame)
    // Order of drawing determines layer visibility
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;    // Use Graphics2D for better rendering control
        tileM.draw(g2);     // Draw background tiles first
        player.draw(g2);    // Draw player on top of tiles

        hud.update(player.hp, player.stamina, player.weapon.getName());  // Update HUD stats
        hud.draw(g2);  // Draw HUD visuals

        // (Ahmed) Dialogue Box - only if line is not empty
        if (!dialogue.getLine().equals("")) {
        // Draw background box
        g2.setColor(new Color(0, 0, 0, 200)); // semi-transparent black
        g2.fillRoundRect(30, screenHeight - 120, screenWidth - 60, 80, 20, 20);

        // Draw text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.drawString(dialogue.getLine(), 50, screenHeight - 80);
    }   
    
        g2.dispose();       // Clean up graphics resources

        
    }
}