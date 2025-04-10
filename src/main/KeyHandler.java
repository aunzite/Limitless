package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Handles keyboard input for the game by implementing KeyListener interface
// Tracks the state of movement keys (WASD) and modifier keys (Shift)
public class KeyHandler implements KeyListener {
    
    // Movement state tracking variables
    public boolean shiftPressed;  // Sprint modifier key state
    public boolean upPressed;     // W key state for upward movement
    public boolean downPressed;   // S key state for downward movement
    public boolean leftPressed;   // A key state for left movement
    public boolean rightPressed;  // D key state for right movement
    
    @Override
    // Required by KeyListener interface but unused in this implementation
    // Called when a key is typed (pressed and released)
    public void keyTyped(KeyEvent e) {
        // Not used in current implementation
    }

    @Override
    // Handles key press events and updates movement flags
    // Sets the corresponding movement flag to true when a key is pressed
    public void keyPressed(KeyEvent e) {
        // Get the numerical code of the pressed key
        int code = e.getKeyCode();
        
        // Update movement flags based on WASD keys and Shift
        switch(code) {
            case KeyEvent.VK_W -> upPressed = true;     // Enable up movement
            case KeyEvent.VK_S -> downPressed = true;   // Enable down movement
            case KeyEvent.VK_A -> leftPressed = true;   // Enable left movement
            case KeyEvent.VK_D -> rightPressed = true;  // Enable right movement
            case KeyEvent.VK_SHIFT -> shiftPressed = true;  // Enable sprint modifier
        }
    }

    @Override
    // Handles key release events and updates movement flags
    // Sets the corresponding movement flag to false when a key is released
    public void keyReleased(KeyEvent e) {
        // Get the numerical code of the released key
        int code = e.getKeyCode();
        
        // Reset movement flags based on WASD keys and Shift
        switch(code) {
            case KeyEvent.VK_W -> upPressed = false;     // Disable up movement
            case KeyEvent.VK_S -> downPressed = false;   // Disable down movement
            case KeyEvent.VK_A -> leftPressed = false;   // Disable left movement
            case KeyEvent.VK_D -> rightPressed = false;  // Disable right movement
            case KeyEvent.VK_SHIFT -> shiftPressed = false;  // Disable sprint modifier
        }
    }
}