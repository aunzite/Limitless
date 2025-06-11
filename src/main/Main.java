/////////////////////////////////////////////////////////////////////////////
// Limitless
// Main.java
// 
// Description: Main entry point for the game including:
// - Game initialization
// - Window setup
// - Main game loop
// - Resource management
/////////////////////////////////////////////////////////////////////////////

package main;

import javax.swing.JFrame;

// Main entry point for the game
public class Main {
    
    // Entry point of the application
    // Creates and configures the game window
    public static void main(String[] args) {
        // Create and set up the game window
        JFrame window = new JFrame();                              // Create window container
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     // Enable proper program termination
        window.setResizable(false);                                // Lock window size
        window.setTitle("Limitless");                           // Set window title bar text

        // Initialize game components
        GamePanel gamePanel = new GamePanel(window);               // Create main game panel with window reference
        window.add(gamePanel);                                     // Add game panel to window

        // Configure window display
        window.pack();                                             // Resize window to fit game panel
        window.setLocationRelativeTo(null);                        // Center window on screen
        window.setVisible(true);                                   // Display the window

        gamePanel.setupGame();
        gamePanel.startGameThread();                               // Begin game loop execution
    }
}