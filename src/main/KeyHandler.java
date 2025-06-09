/////////////////////////////////////////////////////////////////////////////
// Limitless
// KeyHandler.java
// 
// Description: Keyboard input handler for the game. This class:
// - Implements KeyListener interface for input handling (Aun)
// - Tracks WASD movement key states (Aun)
// - Manages sprint modifier with shift key (Aun)
// - Adds Enter key support for dialogue interactions (Ahmed)
// - Updates movement flags based on key events (Aun)
/////////////////////////////////////////////////////////////////////////////

package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import entity.HUD;

// Handles keyboard input for the game by implementing KeyListener interface
// Tracks the state of movement keys (WASD), Enter, and Shift modifiers
public class KeyHandler implements KeyListener {

    private Saver saver;
    private HUD hud;

    // Movement & input tracking 
    public boolean shiftPressed;     // Sprint modifier key state
    public boolean upPressed;        // W key state for upward movement
    public boolean downPressed;      // S key state for downward movement
    public boolean leftPressed;      // A key state for left movement
    public boolean rightPressed;     // D key state for right movement
    public boolean enterPressed;     // Enter key state for dialogue advancing (Ahmed)
    public boolean ePressed;         // E key state for NPC interaction
    public boolean f11Pressed;       // F11 key state for fullscreen toggle
    public boolean escapePressed;    // Escape key state for quitting
    
    // Add new button states
    public boolean savePressed;    // F5 key for saving
    public boolean loadPressed;    // F6 key for loading
    public boolean deletePressed;  // F7 key for deleting save
    public boolean iPressed;       // I key for inventory

    public KeyHandler(Saver saver, HUD hud) {
        this.saver = saver;
        this.hud = hud;
    }

    public void setHUD(HUD hud) {
        this.hud = hud;
    }

    @Override
    // Required by KeyListener interface but unused in this implementation
    public void keyTyped(KeyEvent e) {
        // Not used (Ahmed)
    }

    @Override
    // Handles key press events and updates input flags
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Toggle input flags based on key pressed
        switch(code) {
            case KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_SHIFT -> shiftPressed = true;
            case KeyEvent.VK_ENTER -> enterPressed = true;  // Dialogue confirm (Ahmed)
            case KeyEvent.VK_E -> ePressed = true;         // NPC interaction
            case KeyEvent.VK_F11 -> f11Pressed = true;     // Fullscreen toggle
            case KeyEvent.VK_ESCAPE -> escapePressed = true; // Escape key for quitting
            case KeyEvent.VK_F5 -> savePressed = true;
            case KeyEvent.VK_F6 -> loadPressed = true;
            case KeyEvent.VK_F7 -> deletePressed = true;
            case KeyEvent.VK_H -> hud.setShowAttackHistory(!hud.isShowAttackHistory()); //Attack history (Ahmed)
            case KeyEvent.VK_I -> iPressed = true;         // Inventory toggle
        }
        saver.handleInput(savePressed, loadPressed, deletePressed);
    }

    @Override
    // Handles key release events and resets input flags
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        switch(code) {
            case KeyEvent.VK_W -> upPressed = false;
            case KeyEvent.VK_S -> downPressed = false;
            case KeyEvent.VK_A -> leftPressed = false;
            case KeyEvent.VK_D -> rightPressed = false;
            case KeyEvent.VK_SHIFT -> shiftPressed = false;
            case KeyEvent.VK_ENTER -> enterPressed = false; // Dialogue released (Ahmed)
            case KeyEvent.VK_E -> ePressed = false;        // NPC interaction released
            case KeyEvent.VK_F11 -> f11Pressed = false;    // Fullscreen toggle released
            case KeyEvent.VK_ESCAPE -> escapePressed = false; // Escape key released
            case KeyEvent.VK_F5 -> savePressed = false;
            case KeyEvent.VK_F6 -> loadPressed = false;
            case KeyEvent.VK_F7 -> deletePressed = false; 
            case KeyEvent.VK_I -> iPressed = false;        // Inventory toggle released
        }
        saver.handleInput(savePressed, loadPressed, deletePressed);
    }
}
