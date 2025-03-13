package main; // Package declaration

import javax.swing.JFrame; // Import JFrame class

public class Main { // Main class
    public static void main(String[] args) { // Main method
        JFrame window = new JFrame(); // Create a new JFrame object
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set default close operation
        window.setResizable(false); // Make the window non-resizable
        window.setTitle("2D Adventure"); // Set the title of the window

        GamePanel gamePanel = new GamePanel(); // Create a new GamePanel object
        window.add(gamePanel); // Add the GamePanel to the JFrame

        window.pack(); // Pack the JFrame to fit the preferred size of its components

        window.setLocationRelativeTo(null); // Center the window on the screen
        window.setVisible(true); // Make the window visible

        gamePanel.startGameThread(); // Start the game thread
    }
}