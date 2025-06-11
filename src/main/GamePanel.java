/////////////////////////////////////////////////////////////////////////////
// Limitless
// GamePanel.java
// 
// Description: Main game panel that handles core game functionality. This class:
// - Manages the game loop and timing system
// - Handles screen and world settings
// - Controls rendering pipeline and layers
// - Coordinates player and tile updates
// - Manages input processing and game state
/////////////////////////////////////////////////////////////////////////////

package main;

import entity.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import object.SuperObject;
import tile.TileManager;

// Main game panel class that handles the game loop, rendering and updates
// Extends JPanel for GUI functionality and implements Runnable for the game loop
public class GamePanel extends JPanel implements Runnable, MouseListener, MouseMotionListener {

    // Screen settings
    public int screenWidth;
    public int screenHeight;
    public int tileSize;
    
    // World Settings
    public final int maxWorldCol = 69;                       // Total number of columns in world map
    public final int maxWorldRow = 68;                       // Total number of rows in world map

    // Game Components
    private int FPS = 60;                     // Target frames per second
    public TileManager tileM;                 // Manages the game's tiles/map
    public Saver saver;                       // Handles save/load functionality
    public KeyHandler keyH;                   // Handles keyboard input
    private Thread gameThread;                // Main game loop thread
    public CollisionChecker cCheck;           // Handles collision detection
    public Player player;                     // Player entity
    public HUD hud;                          // HUD object
    public Dialogue dialogue;                 // Dialogue system
    public AssetSetter aSetter;              // Asset manager
    public SuperObject obj[];                // Game objects array
    public NPC npc;                          // NPC entity
    public Menu menu;                        // Main menu
    public OptionsMenu optionsMenu;          // Options menu
    public PauseMenu pauseMenu;              // Pause menu
    private EnvironmentInteraction[] envInteractions;  // Environmental interactions
    public entity.BossNoxar bossNoxar = null;         // Boss entity
    private List<BossProjectile> bossProjectiles = new ArrayList<>();  // Boss projectiles
    private int lastSpellcastFrame = -1;              // Last spellcast frame
    private int spellcastTargetX = 0, spellcastTargetY = 0;  // Spellcast target position
    private String nearbyItemDesc = "";               // Description of nearby item

    // Game state constants
    public static final int MENU_STATE = 0;           // Main menu state
    public static final int PLAY_STATE = 1;           // Playing state
    public static final int PAUSE_STATE = 2;          // Paused state
    public static final int DIALOGUE_STATE = 3;       // In dialogue state
    public static final int SHRINE_STATE = 4;         // In shrine state
    public static final int OPTIONS_STATE = 5;        // In options menu state
    public static final int GAME_OVER_STATE = 6;      // Game over state
    public static final int WIN_STATE = 7;            // Win state
    public static final int NOXAR_CUTSCENE_STATE = 8; // Noxar's intro cutscene state
    public int gameState = MENU_STATE;                // Current game state
    public boolean gamePaused = false;                // Pause flag
    private boolean inDialogue = false;               // Dialogue flag

    // Shrine platform dimensions
    private static final int PLATFORM_WIDTH = 16;     // Width in tiles
    private static final int PLATFORM_HEIGHT = 8;     // Height in tiles
    private int platformX;                            // Platform X position
    private int platformY;                            // Platform Y position

    // UI and display settings
    public JFrame frame;                              // Main window frame
    private boolean isFullscreen = false;             // Fullscreen flag
    private float saveLoadAlpha = 0f;                 // Save/load UI alpha
    private static final float FADE_SPEED = 0.05f;    // Fade animation speed

    // Game mechanics
    private boolean canPickup = true;                 // Item pickup flag
    private long lastNoxarHitTime = 0;                // Last boss hit time
    public int noxarCutsceneIndex = 0;                // Cutscene progress
    public boolean inNoxarCutscene = false;           // Cutscene flag
    public String[] noxarCutsceneLines;               // Cutscene dialogue

    // Constructor: Initializes the game panel and sets up basic properties
    public GamePanel(JFrame frame) {
        this.frame = frame;
        
        // Set default screen dimensions and calculate tile size
        screenWidth = 1536;
        screenHeight = 864;
        tileSize = screenWidth / 16; // Initial tile size
        
        // Configure panel properties for smooth rendering
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        
        // Initialize core game components in dependency order
        saver = new Saver(this);
        hud = new HUD(this, keyH);  // Create HUD first with null KeyHandler
        keyH = new KeyHandler(saver, hud);  // Create KeyHandler with HUD
        keyH.setGamePanel(this);
        hud.setKeyHandler(keyH);  // Set KeyHandler in HUD
        
        // Load game settings and initialize UI components
        GameSettings.getInstance().loadSettings();
        menu = new Menu(this);
        optionsMenu = new OptionsMenu(this);
        pauseMenu = new PauseMenu(this);
        
        // Set up input handling
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        // Initialize game world components
        tileM = new TileManager(this);
        cCheck = new CollisionChecker(this);
        player = new Player(this, keyH);
        player.weapon = null;
        npc = new NPC(this, keyH);
        dialogue = new Dialogue(this);
        aSetter = new AssetSetter(this);
        
        // Position NPC in the game world
        npc.worldX = (screenWidth / 2) + (tileSize * 15);
        npc.worldY = (screenHeight / 2) + (tileSize * 7);    // Position NPC slightly below the water
        
        // Initialize environmental interactions array
        envInteractions = new EnvironmentInteraction[5];  // Increased size to 5
        
        // Set up environmental interaction points with their dialogue
        setupEnvironmentalInteractions();
        
        // Configure window state
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameState = MENU_STATE;

        // Load Noxar's cutscene dialogue from file
        loadNoxarCutscene();
    }

    // Sets up all environmental interaction points in the game world
    private void setupEnvironmentalInteractions() {
        // Spawn Ruins - Starting area interaction
        String[] spawnRuinsDialogue = {
            "These ruins mark the beginning of your journey.",
            "Their weathered stones hold memories of those who came before.",
            "The path ahead leads to the forest below.",
            "It fills you with determination!"
        };
        envInteractions[0] = new EnvironmentInteraction(this, keyH, "Spawn Ruins", 
            tileSize * 12,  // X position (12,7)
            tileSize * 7,   // Y position
            tileSize * 2,   // Interaction radius
            spawnRuinsDialogue);
        
        // Ancient Pond - First major landmark
        String[] pondDialogue = {
            "The water's surface ripples gently, reflecting the pale light above.",
            "Something about this place feels ancient, like it holds memories of better times.",
            "You feel a strange pull towards the forest below.",
            "It fills you with determination!"
        };
        envInteractions[1] = new EnvironmentInteraction(this, keyH, "Ancient Pond", 
            tileSize * 35,  // X position (35,13)
            tileSize * 13,  // Y position
            tileSize * 2,   // Interaction radius
            pondDialogue);
            
        // Arrow Ruins - Mid-game landmark
        String[] ruinsDialogue = {
            "The arrow-shaped ruins point downward, towards the forest.",
            "Their weathered surface tells stories of countless battles fought here.",
            "The path below seems to call to you.",
            "It fills you with determination!"
        };
        envInteractions[2] = new EnvironmentInteraction(this, keyH, "Arrow Ruins",
            tileSize * 51,  // X position (51,6)
            tileSize * 6,   // Y position
            tileSize * 2,   // Interaction radius
            ruinsDialogue);
            
        // Forest Edge - Pre-boss area
        String[] forestDialogue = {
            "The forest's edge seems to pulse with an otherworldly energy.",
            "Shadows dance between the trees, beckoning you forward.",
            "Your journey truly begins below.",
            "It fills you with determination!"
        };
        envInteractions[3] = new EnvironmentInteraction(this, keyH, "Forest Edge",
            tileSize * 61,  // X position (61,20)
            tileSize * 20,  // Y position
            tileSize * 2,   // Interaction radius
            forestDialogue);
        
        // Ancient Shrine - Boss area entrance
        String[] shrineDialogue = {
            "The ancient shrine stands before you.",
            "Its weathered stones seem to pulse with energy.",
            "Press E to enter."
        };
        envInteractions[4] = new EnvironmentInteraction(this, keyH, "Ancient Shrine", 
            2168,  // X position (actual shrine location)
            4116,  // Y position (actual shrine location)
            tileSize * 2,   // Interaction radius
            shrineDialogue);
    }

    // Loads Noxar's cutscene dialogue from file
    private void loadNoxarCutscene() {
        try {
            // Read all lines from the cutscene file
            java.util.List<String> lines = Files.readAllLines(Paths.get("res/dialogue/noxar.txt"));
            java.util.List<String> cutscene = new java.util.ArrayList<>();
            StringBuilder current = new StringBuilder();
            
            // Process the file line by line, combining multi-line dialogue
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    if (current.length() > 0) {
                        cutscene.add(current.toString().trim());
                        current.setLength(0);
                    }
                } else {
                    if (current.length() > 0) current.append("\n");
                    current.append(line);
                }
            }
            
            // Add any remaining dialogue
            if (current.length() > 0) cutscene.add(current.toString().trim());
            noxarCutsceneLines = cutscene.toArray(new String[0]);
        } catch (Exception e) {
            // Fallback: single error line if file loading fails
            noxarCutsceneLines = new String[]{"[Error loading Noxar cutscene]\n" + e.getMessage()};
        }
    }

    // Toggles between fullscreen and windowed mode
    private void toggleFullscreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        if (!isFullscreen) {
            // Enter fullscreen mode
            gd.setFullScreenWindow(frame);
            isFullscreen = true;
        } else {
            // Exit fullscreen mode
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

    // Sets up initial game state and positions
    public void setupGame() {
        // Initialize game objects and set their positions
        aSetter.setObject();
        
        // Set initial player position
        player.worldX = tileSize * 12;
        player.worldY = tileSize * 10;
        player.direction = "down";
    }

    // Starts the main game loop thread
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    // Main game loop implementation
    // Handles timing, updates, and rendering at a fixed rate (60 FPS)
    public void run() {
        // Calculate time per frame in nanoseconds
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        
        // Main game loop
        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            
            // Update and render when enough time has passed
            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    // Updates game state based on current game state
    public void update() {
        // Handle different game states
        switch(gameState) {
            case MENU_STATE:
                menu.update();
                break;
            case PLAY_STATE:
                if (!gamePaused) {
                    updateGameState();
                }
                break;
            case PAUSE_STATE:
                pauseMenu.update();
                break;
            case DIALOGUE_STATE:
                // Dialogue state updates handled by Dialogue class
                break;
            case SHRINE_STATE:
                // Shrine state updates handled by Shrine class
                break;
            case OPTIONS_STATE:
                optionsMenu.update();
                break;
            case GAME_OVER_STATE:
                // Game over state updates
                break;
            case WIN_STATE:
                // Win state updates
                break;
            case NOXAR_CUTSCENE_STATE:
                // Cutscene state updates
                break;
        }
    }

    // Updates game state during play
    private void updateGameState() {
        // Update player
        player.update();
        
        // Update NPC
        npc.update();
        
        // Update boss if present
        if (bossNoxar != null) {
            bossNoxar.update();
            updateBossProjectiles();
        }
        
        // Update environmental interactions
        for (EnvironmentInteraction interaction : envInteractions) {
            if (interaction != null) {
                interaction.update();
            }
        }
        
        // Handle item pickup cooldown
        if (!canPickup) {
            if (System.currentTimeMillis() - lastNoxarHitTime > 500) {
                canPickup = true;
            }
        }
    }

    // Updates boss projectiles and handles their movement/collision
    private void updateBossProjectiles() {
        // Update existing projectiles
        for (int i = bossProjectiles.size() - 1; i >= 0; i--) {
            BossProjectile projectile = bossProjectiles.get(i);
            projectile.update();
            
            // Remove projectiles that are out of bounds or have hit something
            if (projectile.isOutOfBounds()) {
                bossProjectiles.remove(i);
            }
        }
    }

    @Override
    // Main rendering method
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        // Enable anti-aliasing for smoother graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw based on current game state
        switch(gameState) {
            case MENU_STATE:
                menu.draw(g2);
                break;
            case PLAY_STATE:
                drawGameState(g2, 0, 0);
                break;
            case PAUSE_STATE:
                drawGameState(g2, 0, 0);
                pauseMenu.draw(g2);
                break;
            case DIALOGUE_STATE:
                drawGameState(g2, 0, 0);
                dialogue.draw(g2);
                break;
            case SHRINE_STATE:
                drawGameState(g2, 0, 0);
                break;
            case OPTIONS_STATE:
                optionsMenu.draw(g2);
                break;
            case GAME_OVER_STATE:
                drawGameState(g2, 0, 0);
                drawGameOver(g2);
                break;
            case WIN_STATE:
                drawGameState(g2, 0, 0);
                drawWinScreen(g2);
                break;
            case NOXAR_CUTSCENE_STATE:
                drawNoxarCutscene(g2);
                break;
        }
        
        // Dispose of graphics context
        g2.dispose();
    }

    // Draws the main game state including world, entities, and UI
    private void drawGameState(Graphics2D g2, int xOffset, int yOffset) {
        // Draw tiles
        tileM.draw(g2);
        
        // Draw game objects
        drawGameObjects(g2);
        
        // Draw NPC
        npc.draw(g2);
        
        // Draw environmental interactions
        for (EnvironmentInteraction interaction : envInteractions) {
            if (interaction != null) {
                interaction.draw(g2);
            }
        }
        
        // Draw player
        player.draw(g2);
        
        // Draw boss if present
        if (bossNoxar != null) {
            bossNoxar.draw(g2, this);
            drawBossHealthBar(g2, bossNoxar);
        }
        
        // Draw boss projectiles
        for (BossProjectile projectile : bossProjectiles) {
            projectile.draw(g2);
        }
        
        // Draw HUD
        hud.draw(g2, player.weapon);
        
        // Draw save/load instructions if needed
        if (saveLoadAlpha > 0) {
            drawSaveLoadInstructions(g2);
        }
    }

    // Draws all game objects in the world
    private void drawGameObjects(Graphics2D g2) {
        // Track nearby items for interaction
        boolean nearApple = false, nearSolthorn = false;
        int solthornScreenX = -1, solthornScreenY = -1, solthornObjIndex = -1;
        int solthornQuantity = 1; // For future-proofing, but Solthorn is unique
        
        // Draw and check each object
        for(int i = 0; i < obj.length; i++) {
            if(obj[i] != null) {
                obj[i].draw(g2, this);
                
                // Check if player is near this item
                int distance = (int) Math.sqrt(
                    Math.pow(obj[i].worldX - player.worldX, 2) + 
                    Math.pow(obj[i].worldY - player.worldY, 2)
                );
                
                // Handle nearby items
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
    }

    // Draws save/load instructions with fade effect
    private void drawSaveLoadInstructions(Graphics2D g2) {
        // Update alpha for fade effect
        if (saveLoadAlpha < 1.0f) {
            saveLoadAlpha += FADE_SPEED;
        }
        
        // Draw semi-transparent background
        g2.setColor(new Color(0, 0, 0, (int)(200 * saveLoadAlpha)));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw instructions text
        g2.setColor(new Color(255, 255, 255, (int)(255 * saveLoadAlpha)));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        
        String[] instructions = {
            "F5: Save Game",
            "F6: Load Game",
            "F7: Delete Save"
        };
        
        int y = screenHeight / 2 - 50;
        for (String instruction : instructions) {
            int x = (screenWidth - g2.getFontMetrics().stringWidth(instruction)) / 2;
            g2.drawString(instruction, x, y);
            y += 30;
        }
    }

    // Draws the boss health bar
    private void drawBossHealthBar(Graphics2D g2, entity.BossNoxar boss) {
        // Calculate health bar dimensions
        int barWidth = 400;
        int barHeight = 30;
        int x = (screenWidth - barWidth) / 2;
        int y = 50;
        
        // Draw background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(x - 2, y - 2, barWidth + 4, barHeight + 4);
        
        // Draw health bar
        g2.setColor(Color.RED);
        g2.fillRect(x, y, barWidth, barHeight);
        
        // Draw current health
        g2.setColor(Color.GREEN);
        int currentWidth = (int)((boss.health / (float)boss.maxHealth) * barWidth);
        g2.fillRect(x, y, currentWidth, barHeight);
        
        // Draw border
        g2.setColor(Color.WHITE);
        g2.drawRect(x, y, barWidth, barHeight);
        
        // Draw boss name
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        String name = boss.name;
        int nameWidth = g2.getFontMetrics().stringWidth(name);
        g2.drawString(name, x + (barWidth - nameWidth) / 2, y - 10);
    }

    // Mouse event handlers
    @Override
    public void mousePressed(MouseEvent e) {
        // Handle mouse press based on game state
        switch(gameState) {
            case MENU_STATE:
                menu.mousePressed(e);
                break;
            case OPTIONS_STATE:
                optionsMenu.mousePressed(e);
                break;
            case PAUSE_STATE:
                pauseMenu.mousePressed(e);
                break;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Handle mouse drag based on game state
        switch(gameState) {
            case MENU_STATE:
                menu.mouseDragged(e);
                break;
            case OPTIONS_STATE:
                optionsMenu.mouseDragged(e);
                break;
            case PAUSE_STATE:
                pauseMenu.mouseDragged(e);
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Handle mouse release based on game state
        switch(gameState) {
            case MENU_STATE:
                menu.mouseReleased(e);
                break;
            case OPTIONS_STATE:
                optionsMenu.mouseReleased(e);
                break;
            case PAUSE_STATE:
                pauseMenu.mouseReleased(e);
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Handle mouse click based on game state
        switch(gameState) {
            case MENU_STATE:
                menu.mouseClicked(e);
                break;
            case OPTIONS_STATE:
                optionsMenu.mouseClicked(e);
                break;
            case PAUSE_STATE:
                pauseMenu.mouseClicked(e);
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Handle mouse enter based on game state
        switch(gameState) {
            case MENU_STATE:
                menu.mouseEntered(e);
                break;
            case OPTIONS_STATE:
                optionsMenu.mouseEntered(e);
                break;
            case PAUSE_STATE:
                pauseMenu.mouseEntered(e);
                break;
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Handle mouse exit based on game state
        switch(gameState) {
            case MENU_STATE:
                menu.mouseExited(e);
                break;
            case OPTIONS_STATE:
                optionsMenu.mouseExited(e);
                break;
            case PAUSE_STATE:
                pauseMenu.mouseExited(e);
                break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Handle mouse movement based on game state
        switch(gameState) {
            case MENU_STATE:
                menu.mouseMoved(e);
                break;
            case OPTIONS_STATE:
                optionsMenu.mouseMoved(e);
                break;
            case PAUSE_STATE:
                pauseMenu.mouseMoved(e);
                break;
        }
    }

    // Draws the game over screen
    private void drawGameOver(Graphics2D g2) {
        // Draw semi-transparent background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw game over text
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 72));
        String text = "GAME OVER";
        FontMetrics fm = g2.getFontMetrics();
        int x = (screenWidth - fm.stringWidth(text)) / 2;
        int y = screenHeight / 2;
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);
        
        // Draw restart hint
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        text = "Press R to restart";
        fm = g2.getFontMetrics();
        x = (screenWidth - fm.stringWidth(text)) / 2;
        y = screenHeight / 2 + 50;
        g2.drawString(text, x, y);
    }

    // Draws the victory screen
    private void drawWinScreen(Graphics2D g2) {
        // Draw semi-transparent background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw victory text
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 72));
        String text = "VICTORY";
        FontMetrics fm = g2.getFontMetrics();
        int x = (screenWidth - fm.stringWidth(text)) / 2;
        int y = screenHeight / 2;
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);
        
        // Draw menu hint
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        text = "Press ESC to return to menu";
        fm = g2.getFontMetrics();
        x = (screenWidth - fm.stringWidth(text)) / 2;
        y = screenHeight / 2 + 50;
        g2.drawString(text, x, y);
    }

    // Draws Noxar's cutscene
    private void drawNoxarCutscene(Graphics2D g2) {
        // Draw semi-transparent background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw cutscene text
        if (noxarCutsceneIndex < noxarCutsceneLines.length) {
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
            String[] lines = noxarCutsceneLines[noxarCutsceneIndex].split("\n");
            int y = screenHeight / 2 - (lines.length * 30) / 2;
            
            for (String line : lines) {
                FontMetrics fm = g2.getFontMetrics();
                int x = (screenWidth - fm.stringWidth(line)) / 2;
                g2.setColor(Color.WHITE);
                g2.drawString(line, x, y);
                y += 30;
            }
        }
    }

    // Resets player state to default values
    private void resetPlayerState() {
        player.worldX = tileSize * 12;
        player.worldY = tileSize * 10;
        player.direction = "down";
        player.hp = 100;
        player.stamina = 1000;
        player.weapon = null;
    }
}