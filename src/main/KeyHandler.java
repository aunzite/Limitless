package main; // Package declaration

import java.awt.event.KeyEvent; // Import KeyEvent class
import java.awt.event.KeyListener; // Import KeyListener interface

public class KeyHandler implements KeyListener{ // KeyHandler class implementing KeyListener

    public boolean shiftPressed, upPressed, downPressed, leftPressed, rightPressed; // Booleans to track key presses
    
    @Override
    public void keyTyped(KeyEvent e) { // Method called when a key is typed (not used)
    }

    @Override
    public void keyPressed(KeyEvent e) { // Method called when a key is pressed
        int code = e.getKeyCode(); // Get the key code of the pressed key
        
        if (code == KeyEvent.VK_W){ // If 'W' key is pressed
            upPressed = true; // Set upPressed to true
        }
        if (code == KeyEvent.VK_S){ // If 'S' key is pressed
            downPressed = true; // Set downPressed to true
        }
        if (code == KeyEvent.VK_A){ // If 'A' key is pressed
            leftPressed = true; // Set leftPressed to true
        }
        if (code == KeyEvent.VK_D){ // If 'D' key is pressed
            rightPressed = true; // Set rightPressed to true
        }
        if (code == KeyEvent.VK_SHIFT){ // If 'Shift' key is pressed
            shiftPressed = true; // Set shiftPressed to true
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { // Method called when a key is released

        int code = e.getKeyCode(); // Get the key code of the released key

        if (code == KeyEvent.VK_W){ // If 'W' key is released
            upPressed = false; // Set upPressed to false
        }
        if (code == KeyEvent.VK_S){ // If 'S' key is released
            downPressed = false; // Set downPressed to false
        }
        if (code == KeyEvent.VK_A){ // If 'A' key is released
            leftPressed = false; // Set leftPressed to false
        }
        if (code == KeyEvent.VK_D){ // If 'D' key is released
            rightPressed = false; // Set rightPressed to false
        }
        if (code == KeyEvent.VK_SHIFT){ //If 'Shift' key is released
            shiftPressed = false; // Set shiftPressed to false
        }
    }

}