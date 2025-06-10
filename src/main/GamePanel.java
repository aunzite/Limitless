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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import object.SuperObject;
import tile.TileManager;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private EnvironmentInteraction[] envInteractions;  // Environmental interactions
    public entity.BossNoxar bossNoxar = null;
    private List<BossProjectile> bossProjectiles = new ArrayList<>();
    private int lastSpellcastFrame = -1;
    private int spellcastTargetX = 0, spellcastTargetY = 0;

    // Game state
    public static final int MENU_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int PAUSE_STATE = 2;
    public static final int DIALOGUE_STATE = 3;
    public static final int SHRINE_STATE = 4;  // New state for shrine interior
    public static final int OPTIONS_STATE = 5;  // Added missing constant
    public static final int GAME_OVER_STATE = 6;  // New state for game over screen
    public static final int WIN_STATE = 7; // New state for win screen
    public static final int NOXAR_CUTSCENE_STATE = 8; // New state for Noxar's intro cutscene
    public int gameState = MENU_STATE;
    public boolean gamePaused = false;
    private boolean inDialogue = false;

    // Shrine platform dimensions
    private static final int PLATFORM_WIDTH = 16;  // Width in tiles
    private static final int PLATFORM_HEIGHT = 8;  // Height in tiles
    private int platformX;  // X position of platform
    private int platformY;  // Y position of platform

    public JFrame frame;  // Changed to public
    private boolean isFullscreen = false;

    private float saveLoadAlpha = 0f;
    private static final float FADE_SPEED = 0.05f;

    // Add this field to GamePanel:
    private boolean canPickup = true;

    // Add this field to GamePanel:
    private long lastNoxarHitTime = 0;

    // Add these fields to GamePanel:
    public int noxarCutsceneIndex = 0;
    public boolean inNoxarCutscene = false;

    // Change this field to public:
    public String[] noxarCutsceneLines;

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
        hud = new HUD(this, keyH);  // Create HUD first with null KeyHandler
        keyH = new KeyHandler(saver, hud);  // Create KeyHandler with HUD
        keyH.setGamePanel(this);
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
        npc.worldX = (screenWidth / 2) + (tileSize * 15);
        npc.worldY = (screenHeight / 2) + (tileSize * 7);    // Position NPC slightly below the water
        
        // Initialize environmental interactions
        envInteractions = new EnvironmentInteraction[5];  // Increased size to 5
        
        // Spawn ruins interaction
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
        
        // Pond interaction
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
            
        // Arrow ruins interaction
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
            
        // Forest edge interaction
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
        
        // Shrine interaction
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
        
        // Set frame to maximized
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Set initial game state to MENU_STATE
        gameState = MENU_STATE;

        // Load the cutscene lines from file
        try {
            java.util.List<String> lines = Files.readAllLines(Paths.get("res/dialogue/noxar.txt"));
            java.util.List<String> cutscene = new java.util.ArrayList<>();
            StringBuilder current = new StringBuilder();
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
            if (current.length() > 0) cutscene.add(current.toString().trim());
            noxarCutsceneLines = cutscene.toArray(new String[0]);
        } catch (Exception e) {
            // Fallback: single error line
            noxarCutsceneLines = new String[]{"[Error loading Noxar cutscene]\n" + e.getMessage()};
        }
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

        if (gameState == GAME_OVER_STATE) {
            main.AudioManager.getInstance().stopMusic();
            if (keyH.enterPressed) {
                // Reset player state before returning to menu
                resetPlayerState();
                gameState = MENU_STATE;
                main.AudioManager.getInstance().playMainMenuMusic();
                keyH.enterPressed = false;
            } else if (keyH.escapePressed) {
                // Exit game
                System.exit(0);
            }
            return;
        }
        
        if (gameState == SHRINE_STATE) {
            // Check for player death
            if (player.hp <= 0) {
                gameState = GAME_OVER_STATE;
                return;
            }
            // Initialize boss if not already
            if (bossNoxar == null) {
                // Place boss on the right side of the platform
                int bossX = platformX + (int)(PLATFORM_WIDTH * tileSize * 0.75);
                int bossY = platformY + (PLATFORM_HEIGHT * tileSize) / 2 - 64;
                bossNoxar = new entity.BossNoxar(bossX, bossY);
            }
            // Update boss
            bossNoxar.update();
            // Update player animation and state
            player.update();
            // Handle spellcast projectile spawn
            if (bossNoxar.isCasting) {
                int currentFrame = bossNoxar.castFrame;
                if (lastSpellcastFrame != currentFrame) {
                    // On frame 3 (middle of 0-7), spawn projectile
                    if (currentFrame == 3) {
                        // Target player's position at cast start
                        spellcastTargetX = player.worldX + player.playerHitbox.x + player.playerHitbox.width / 2;
                        spellcastTargetY = player.worldY + player.playerHitbox.y + player.playerHitbox.height / 2;
                        int projX = bossNoxar.x + bossNoxar.width / 2;
                        int projY = bossNoxar.y + bossNoxar.height / 2;
                        bossProjectiles.add(new BossProjectile(projX, projY, spellcastTargetX, spellcastTargetY));
                    }
                    lastSpellcastFrame = currentFrame;
                }
            } else {
                lastSpellcastFrame = -1;
            }
            // Update projectiles
            for (int i = 0; i < bossProjectiles.size(); i++) {
                BossProjectile proj = bossProjectiles.get(i);
                proj.update();
                // Check collision with player
                Rectangle pRect = new Rectangle(
                    player.worldX + player.playerHitbox.x,
                    player.worldY + player.playerHitbox.y,
                    player.playerHitbox.width,
                    player.playerHitbox.height
                );
                if (proj.active && proj.getBounds().intersects(pRect)) {
                    player.hp -= proj.damage;
                    proj.active = false;
                }
                // Remove if off screen or inactive
                if (!proj.active || proj.x < 0 || proj.x > screenWidth || proj.y < 0 || proj.y > screenHeight) {
                    bossProjectiles.remove(i);
                    i--;
                }
            }
            // Handle shrine interior state
            if (keyH.escapePressed) {
                gameState = PLAY_STATE;
                keyH.escapePressed = false;
            }

            // Handle inventory toggle
            if (keyH.iPressed) {
                player.inventory.toggle();
                keyH.iPressed = false;
            }

            // Handle player movement in shrine (NO collision checks)
            if (!player.inventory.isOpen()) {
                boolean isMoving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;
                // Only allow running if player has stamina
                boolean isRunning = keyH.shiftPressed && isMoving && player.stamina > 0;
                // Track previous animation state
                String prevAnimationState = player.animationState;
                // Update animation state and spriteNum immediately when movement starts
                if (!isMoving) {
                    player.animationState = "idle";
                    player.spriteCounter++;
                    if (player.spriteCounter > 12) {
                        player.spriteNum = (player.spriteNum == 1) ? 2 : 1;
                        player.spriteCounter = 0;
                    }
                } else if (isRunning && player.stamina > 0) {
                    player.animationState = "run";
                    if (!prevAnimationState.equals("run")) {
                        player.spriteNum = 10; // Start at first run frame
                    }
                    if (player.spriteCounter > 12) {
                        player.spriteNum = (player.spriteNum >= 17) ? 10 : player.spriteNum + 1;
                        player.spriteCounter = 0;
                    }
                } else {
                    player.animationState = "walk";
                    if (!prevAnimationState.equals("walk")) {
                        player.spriteNum = 5; // Start at first walk frame
                    }
                    if (player.spriteCounter > 12) {
                        player.spriteNum = (player.spriteNum >= 9) ? 5 : player.spriteNum + 1;
                        player.spriteCounter = 0;
                    }
                }

                // Handle sword swinging
                if (keyH.spacePressed && player.weapon != null) {
                    player.handleSpacePressed();
                    // Check if sword hit the boss with cooldown
                    if (bossNoxar != null) {
                        Rectangle swordRect = new Rectangle(
                            player.worldX + player.playerHitbox.x - 20,
                            player.worldY + player.playerHitbox.y - 20,
                            player.playerHitbox.width + 40,
                            player.playerHitbox.height + 40
                        );
                        long now = System.currentTimeMillis();
                        if (swordRect.intersects(bossNoxar.getBounds()) && now - lastNoxarHitTime >= 1000) {
                            bossNoxar.takeDamage(50);
                            lastNoxarHitTime = now;
                        }
                    }
                }

                // Update player position based on movement
                // Regenerate stamina when not running
                if (!isRunning) {
                    hud.regenerateStamina(isMoving);
                    // Update player's stamina from HUD
                    player.stamina = hud.getStamina();
                }
                // Set direction and move player
                int moveSpeed = isRunning ? 8 : 4;
                if (keyH.upPressed) {
                    player.direction = "up";
                    player.worldY -= moveSpeed;
                }
                if (keyH.downPressed) {
                    player.direction = "down";
                    player.worldY += moveSpeed;
                }
                if (keyH.leftPressed) {
                    player.direction = "left";
                    player.worldX -= moveSpeed;
                }
                if (keyH.rightPressed) {
                    player.direction = "right";
                    player.worldX += moveSpeed;
                }
                // Keep player within platform bounds ONLY
                int platformX = (screenWidth - (PLATFORM_WIDTH * tileSize)) / 2;
                int platformY = (screenHeight - (PLATFORM_HEIGHT * tileSize)) / 2;
                if (player.worldX < platformX + tileSize) {
                    player.worldX = platformX + tileSize;
                }
                if (player.worldX > platformX + ((PLATFORM_WIDTH - 1) * tileSize)) {
                    player.worldX = platformX + ((PLATFORM_WIDTH - 1) * tileSize);
                }
                if (player.worldY < platformY + tileSize) {
                    player.worldY = platformY + tileSize;
                }
                if (player.worldY > platformY + ((PLATFORM_HEIGHT - 1) * tileSize)) {
                    player.worldY = platformY + ((PLATFORM_HEIGHT - 1) * tileSize);
                }
            }

            // Update HUD
            String weaponName = player.weapon != null ? player.weapon.getName() : "No Weapon";
            hud.update(player.hp, player.stamina, weaponName, 
                keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed);

            // Check for win condition
            if (bossNoxar != null && bossNoxar.health <= 0 && bossNoxar.isDying == false && bossNoxar.isDead == false) {
                bossNoxar.isDying = true;
                bossNoxar.hurtFrame = 0;
                bossNoxar.lastFrameTime = System.currentTimeMillis();
            }
            if (bossNoxar != null && bossNoxar.health <= 0 && bossNoxar.isDead) {
                gameState = WIN_STATE;
                return;
            }
            return;
        }
        
        // Only update NPC and environmental interactions if inventory is not open
        if (!player.inventory.isOpen()) {
            npc.update();
            // Update environmental interactions
            for (EnvironmentInteraction interaction : envInteractions) {
                if (interaction != null) {
                    interaction.update();
                }
            }
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
        if (keyH.ePressed && !dialogue.getLine().equals("")) {
            dialogue.clear();
            keyH.ePressed = false; // Prevent multiple triggers
        }
        
        // Handle pause menu toggle
        if (keyH.escapePressed && gameState == PLAY_STATE) {
            gameState = PAUSE_STATE;
            keyH.escapePressed = false;
        }

        // Handle win condition
        if (gameState == WIN_STATE) {
            main.AudioManager.getInstance().stopMusic();
            if (keyH.enterPressed) {
                resetPlayerState();
                gameState = MENU_STATE;
                main.AudioManager.getInstance().playMainMenuMusic();
                keyH.enterPressed = false;
            } else if (keyH.escapePressed) {
                System.exit(0);
            }
            return;
        }

        // Handle NOXAR_CUTSCENE_STATE
        if (gameState == NOXAR_CUTSCENE_STATE) {
            // Prevent player and boss actions
            if (keyH.enterPressed) {
                noxarCutsceneIndex++;
                keyH.enterPressed = false;
                if (noxarCutsceneIndex >= noxarCutsceneLines.length) {
                    inNoxarCutscene = false;
                    gameState = SHRINE_STATE;
                    // Start battle music
                    main.AudioManager.getInstance().stopMusic();
                    try { main.AudioManager.getInstance().playBossFightMusic(); } catch (Exception e) {}
                }
            }
            return;
        }
    }

    // Renders the game (called every frame)
    // Order of drawing determines layer visibility
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == MENU_STATE) {
            menu.draw(g2);
        } else if (gameState == OPTIONS_STATE) {
            optionsMenu.draw(g2);
        } else if (gameState == PAUSE_STATE) {
            pauseMenu.draw(g2);
        } else if (gameState == GAME_OVER_STATE) {
            // Draw black background
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            
            // Draw "Game Over" text
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 72));
            String gameOverText = "GAME OVER";
            int textWidth = g2.getFontMetrics().stringWidth(gameOverText);
            g2.drawString(gameOverText, (screenWidth - textWidth) / 2, screenHeight / 3);
            
            // Draw instructions
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 36));
            String enterText = "Press ENTER to return to main menu";
            String escapeText = "Press ESC to exit game";
            int enterWidth = g2.getFontMetrics().stringWidth(enterText);
            int escapeWidth = g2.getFontMetrics().stringWidth(escapeText);
            g2.drawString(enterText, (screenWidth - enterWidth) / 2, screenHeight / 2);
            g2.drawString(escapeText, (screenWidth - escapeWidth) / 2, screenHeight / 2 + 50);
        } else if (gameState == SHRINE_STATE) {
            // Draw black background
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            
            // Calculate platform position (centered)
            platformX = (screenWidth - (PLATFORM_WIDTH * tileSize)) / 2;
            platformY = (screenHeight - (PLATFORM_HEIGHT * tileSize)) / 2;
            
            // Draw platform using dfloor.png tiles
            for (int x = 0; x < PLATFORM_WIDTH; x++) {
                for (int y = 0; y < PLATFORM_HEIGHT; y++) {
                    g2.drawImage(tileM.tile[10].image, 
                        platformX + (x * tileSize), 
                        platformY + (y * tileSize), 
                        tileSize, tileSize, null);
                }
            }
            
            // Draw player at current position (direct, not using camera offset)
            player.drawAt(g2, player.worldX, player.worldY);
            // Update boss with player position
            if (bossNoxar != null) {
                bossNoxar.setPlayerPosition(player.worldX, player.worldY);
            }
            // Draw boss
            if (bossNoxar != null) {
                bossNoxar.draw(g2, this);
            }
            // Draw boss projectiles
            for (BossProjectile proj : bossProjectiles) {
                proj.draw(g2);
            }
            // Draw boss health bar at top of screen
            drawBossHealthBar(g2, bossNoxar);
            
            // Draw HUD
            hud.draw(g2, player.weapon);
            // Draw inventory overlay and inventory/items on top of everything
            if (player.inventory.isOpen()) {
                g2.setColor(new Color(0, 0, 0, 128));
                g2.fillRect(0, 0, screenWidth, screenHeight);
                player.inventory.draw(g2);
            }
            return;
        } else if (gameState == WIN_STATE) {
            // Draw black background
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            // Draw WIN text
            g2.setColor(Color.GREEN);
            g2.setFont(new Font("Arial", Font.BOLD, 80));
            String winText = "YOU WIN!";
            int textWidth = g2.getFontMetrics().stringWidth(winText);
            g2.drawString(winText, (screenWidth - textWidth) / 2, screenHeight / 3);
            // Draw instructions
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 36));
            String enterText = "Press ENTER to return to main menu";
            String escapeText = "Press ESC to exit game";
            int enterWidth = g2.getFontMetrics().stringWidth(enterText);
            int escapeWidth = g2.getFontMetrics().stringWidth(escapeText);
            g2.drawString(enterText, (screenWidth - enterWidth) / 2, screenHeight / 2);
            g2.drawString(escapeText, (screenWidth - escapeWidth) / 2, screenHeight / 2 + 50);
            return;
        } else if (gameState == NOXAR_CUTSCENE_STATE) {
            // Draw shrine background and platform
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            int platformX = (screenWidth - (PLATFORM_WIDTH * tileSize)) / 2;
            int platformY = (screenHeight - (PLATFORM_HEIGHT * tileSize)) / 2;
            for (int x = 0; x < PLATFORM_WIDTH; x++) {
                for (int y = 0; y < PLATFORM_HEIGHT; y++) {
                    g2.drawImage(tileM.tile[10].image, platformX + (x * tileSize), platformY + (y * tileSize), tileSize, tileSize, null);
                }
            }
            // Draw player and boss (static, not moving)
            player.drawAt(g2, player.worldX, player.worldY);
            if (bossNoxar != null) {
                bossNoxar.draw(g2, this);
            }
            // Bounds check for cutscene index
            if (noxarCutsceneIndex >= noxarCutsceneLines.length) return;
            // Draw cutscene dialogue box (like Elaria's)
            g2.setColor(new Color(0,0,0,220));
            int boxH = 180;
            int boxY = screenHeight - boxH - 40;
            int boxX = 60;
            int boxW = screenWidth - 120;
            g2.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(3));
            g2.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
            // Word wrap logic
            String[] lines = noxarCutsceneLines[noxarCutsceneIndex].split("\\n");
            int lineY = boxY + 40;
            int maxWidth = boxW - 40;
            for (String line : lines) {
                java.util.List<String> wrapped = new java.util.ArrayList<>();
                String[] words = line.split(" ");
                StringBuilder current = new StringBuilder();
                for (String word : words) {
                    String test = current.length() == 0 ? word : current + " " + word;
                    int width = g2.getFontMetrics().stringWidth(test);
                    if (width > maxWidth && current.length() > 0) {
                        wrapped.add(current.toString());
                        current = new StringBuilder(word);
                    } else {
                        if (current.length() > 0) current.append(" ");
                        current.append(word);
                    }
                }
                if (current.length() > 0) wrapped.add(current.toString());
                for (String wline : wrapped) {
                    g2.drawString(wline, boxX + 20, lineY);
                    lineY += 32;
                }
            }
            g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 18));
            g2.setColor(new Color(255,255,255,180));
            g2.drawString("Press Enter to continue", screenWidth - 320, boxY + boxH - 20);
            return;
        } else {
            // Normal game rendering
            // Update dynamic values
            screenWidth = getWidth();
            screenHeight = getHeight();

            // Calculate offset for camera
            int xOffset = player.worldX - player.screenX;
            int yOffset = player.worldY - player.screenY;

            // Draw background
            g2.setColor(new Color(0, 0, 0));
            g2.fillRect(0, 0, screenWidth, screenHeight);

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
            
            // Draw environmental interactions
            for (EnvironmentInteraction interaction : envInteractions) {
                if (interaction != null) {
                    interaction.draw(g2);
                }
            }
            
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
        }
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
        
        // Draw environmental interactions
        for (EnvironmentInteraction interaction : envInteractions) {
            if (interaction != null) {
                interaction.draw(g2);
            }
        }
        
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

                // Draw I button hint
                BufferedImage iSprite = ImageIO.read(new File(keyH.iPressed ? "res/buttons/ip.png" : "res/buttons/io.png"));
                int iNaturalWidth = iSprite.getWidth();
                int iNaturalHeight = iSprite.getHeight();
                int iDrawWidth = (int) ((double) spriteSize / iNaturalHeight * iNaturalWidth);
                int iDrawX = x + spriteSize - iDrawWidth;
                g2.drawImage(iSprite, iDrawX, y + verticalSpacing * 3, iDrawWidth, spriteSize, null);
                String iText = "Inventory";
                textWidth = fm.stringWidth(iText);
                g2.drawString(iText, iDrawX - textWidth - 10, y + verticalSpacing * 3 + spriteSize/2 + 5);

                // Draw E button hint
                BufferedImage eSprite = ImageIO.read(new File(keyH.ePressed ? "res/buttons/ep.png" : "res/buttons/eo.png"));
                int eNaturalWidth = eSprite.getWidth();
                int eNaturalHeight = eSprite.getHeight();
                int eDrawWidth = (int) ((double) spriteSize / eNaturalHeight * eNaturalWidth);
                int eDrawX = x + spriteSize - eDrawWidth;
                g2.drawImage(eSprite, eDrawX, y + verticalSpacing * 4, eDrawWidth, spriteSize, null);
                String eText = "Interact";
                textWidth = fm.stringWidth(eText);
                g2.drawString(eText, eDrawX - textWidth - 10, y + verticalSpacing * 4 + spriteSize/2 + 5);

                // Draw Space button hint
                BufferedImage spaceSprite = ImageIO.read(new File(keyH.spacePressed ? "res/buttons/spacep.png" : "res/buttons/spaceo.png"));
                int spaceNaturalWidth = spaceSprite.getWidth();
                int spaceNaturalHeight = spaceSprite.getHeight();
                int spaceDrawWidth = (int) ((double) spriteSize / spaceNaturalHeight * spaceNaturalWidth);
                int spaceDrawX = x + spriteSize - spaceDrawWidth;
                g2.drawImage(spaceSprite, spaceDrawX, y + verticalSpacing * 5, spaceDrawWidth, spriteSize, null);
                String spaceText = "Attack";
                textWidth = fm.stringWidth(spaceText);
                g2.drawString(spaceText, spaceDrawX - textWidth - 10, y + verticalSpacing * 5 + spriteSize/2 + 5);

                // Draw Shift button hint
                BufferedImage shiftSprite = ImageIO.read(new File(keyH.shiftPressed ? "res/buttons/shiftp.png" : "res/buttons/shifto.png"));
                int shiftNaturalWidth = shiftSprite.getWidth();
                int shiftNaturalHeight = shiftSprite.getHeight();
                int shiftDrawWidth = (int) ((double) spriteSize / shiftNaturalHeight * shiftNaturalWidth);
                int shiftDrawX = x + spriteSize - shiftDrawWidth;
                g2.drawImage(shiftSprite, shiftDrawX, y + verticalSpacing * 6, shiftDrawWidth, spriteSize, null);
                String shiftText = "Run";
                textWidth = fm.stringWidth(shiftText);
                g2.drawString(shiftText, shiftDrawX - textWidth - 10, y + verticalSpacing * 6 + spriteSize/2 + 5);

                // Reset composite
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawBossHealthBar(Graphics2D g2, entity.BossNoxar boss) {
        int barWidth = screenWidth / 2;
        int barHeight = 32;
        int x = (screenWidth - barWidth) / 2 + 120; // Shift right by 120 pixels
        int y = 20;
        // Background
        g2.setColor(new Color(40, 40, 40, 220));
        g2.fillRoundRect(x, y, barWidth, barHeight, 16, 16);
        // Health
        float percent = boss.health / (float)boss.maxHealth;
        int healthWidth = (int)(barWidth * percent);
        g2.setColor(new Color(120, 0, 0, 220));
        g2.fillRoundRect(x, y, healthWidth, barHeight, 16, 16);
        // Border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(x, y, barWidth, barHeight, 16, 16);
        // Name
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String label = "Noxar";
        int labelWidth = g2.getFontMetrics().stringWidth(label);
        g2.setColor(Color.WHITE);
        g2.drawString(label, x + (barWidth - labelWidth) / 2, y + barHeight - 8);
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

    private void resetPlayerState() {
        // Reset position to (12,10) tiles
        player.worldX = tileSize * 12;
        player.worldY = tileSize * 10;
        // Reset health and stamina
        player.hp = 100;
        player.stamina = 100;
        // Clear inventory and weapon
        player.weapon = null;
        // Clear inventory by setting all items to null
        for (int i = 0; i < player.inventory.items.length; i++) {
            for (int j = 0; j < player.inventory.items[i].length; j++) {
                player.inventory.items[i][j] = null;
            }
        }
        // Reset direction
        player.direction = "down";
        // Reset animation state
        player.animationState = "idle";
        player.spriteNum = 1;
        player.spriteCounter = 0;
        // Reset NPC sword-giving state
        if (npc != null) {
            try {
                java.lang.reflect.Field field = npc.getClass().getDeclaredField("hasGivenSword");
                field.setAccessible(true);
                field.setBoolean(npc, false);
            } catch (Exception e) {
                // Ignore errors
            }
        }
    }
}