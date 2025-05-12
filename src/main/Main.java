/////////////////////////////////////////////////////////////////////////////
// Limitless
// Main.java
// 
// Description: Main entry point for the game. This class:
// - Creates and configures the game window (Aun)
// - Sets up the main game panel (Aun)
// - Initializes display settings (Aun)
// - Launches the game thread (Aun)
/////////////////////////////////////////////////////////////////////////////

package main;

import javax.swing.JFrame;

// Main class that initializes and launches the game
public class Main {
    
    // Entry point of the application
    // Creates and configures the game window
    public static void main(String[] args) {

        // Create and set up the game window
        JFrame window = new JFrame();                              // Create window container
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     // Enable proper program termination
        window.setResizable(false);                      // Lock window size
        window.setTitle("2D Adventure");                     // Set window title bar text

        // Initialize game components
        GamePanel gamePanel = new GamePanel();                     // Create main game panel
        window.add(gamePanel);                                     // Add game panel to window

        // Configure window display
        window.pack();                                             // Resize window to fit game panel
        window.setLocationRelativeTo(null);                      // Center window on screen
        window.setVisible(true);                                 // Display the window

        gamePanel.setupGame();

        // Start the game
        gamePanel.startGameThread();                               // Begin game loop execution
    }
}