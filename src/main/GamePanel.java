package main; // Package declaration

//imports
import entity.Player;
import java.awt.*;
import javax.swing.JPanel; // Import AWT classes
import tile.TileManager; // Import JPanel class

public class GamePanel extends JPanel implements Runnable{ // GamePanel class extending JPanel and implementing Runnable

    //Screen settings
    final int originalTileSize = 16; // 16x16 tile (character size)
    final int scale = 3; // Scale factor

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 16; // Maximum number of columns on the screen
    public final int maxScreenRow = 12; // Maximum number of rows on the screen
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    //WWORLD SETTINGS
    public final int maxWorldCol = 28;
    public final int maxWorldRow = 28;
    public final int worldWidth = tileSize * maxWorldCol; // 768 pixels
    public final int worldHeight = tileSize * maxWorldRow; // 576 pixels

    //FPS (Frames Per Second)
    int FPS = 60; // Frames per second

    TileManager tileM = new TileManager(this);

    // KeyHandler instance to handle keyboard input
    KeyHandler keyH = new KeyHandler(); // Create a KeyHandler object
    // Thread for the game loop
    Thread gameThread; // Declare a Thread object
    public Player player = new Player(this, keyH); // Create a Player object

    // Constructor to set up the game panel
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set the size of the panel
        this.setBackground(Color.WHITE); // Set the background color to black
        this.setDoubleBuffered(true); // Enable double buffering for smoother rendering
        this.addKeyListener(keyH); // Add the key listener for keyboard input
        this.setFocusable(true); // Make the panel focusable to receive key events
    }

    // Method to start the game thread
    public void startGameThread(){
        gameThread = new Thread(this); // Create a new thread
        gameThread.start(); // Start the thread
    }

    @Override
    // Game loop
    public void run(){

        double drawInterval = 1000000000 / FPS; // 0.01666 seconds per frame
        double delta = 0; // Time difference accumulator
        long lastTime = System.nanoTime(); // Last time in nanoseconds
        long currentTime; // Current time in nanoseconds
        long timer = 0; // Timer for FPS calculation
        int drawCount = 0; // Frame counter

        // Main game loop
        while (gameThread != null){

            currentTime = System.nanoTime(); // Get current time

            delta += (currentTime - lastTime) / drawInterval; // Calculate time difference
            timer += (currentTime - lastTime); // Add to timer
            lastTime = currentTime; // Update last time

            if (delta >= 1){
                update(); // Update game state
                repaint(); // Render the game
                delta--; // Decrease delta
                drawCount++; // Increment frame counter
            }
            if (timer >= 1000000000){
                System.out.println("FPS: " + drawCount); // Print FPS to console
                drawCount = 0; // Reset frame counter
                timer = 0; // Reset timer
            }
        }
    }

    // Method to update the game state
    public void update(){
        
        player.update(); // Update player position
    }

    // Method to render the game
    public void paintComponent(Graphics g){

        super.paintComponent(g); // Call superclass method
        Graphics2D g2 = (Graphics2D) g; // Cast Graphics to Graphics2D

        tileM.draw(g2); // Draw the tiles
        
        player.draw(g2); // Draw the player

        g2.dispose(); // Dispose of the graphics context
    }
}