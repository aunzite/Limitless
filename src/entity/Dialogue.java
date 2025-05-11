// Description: Stores and manages dialogue for NPCs or other characters (Ahmed)
package entity;

public class Dialogue {
    
    // Keeps track of what the character is saying right now
    private String currentLine;

    // Default constructor
    public Dialogue() {
        currentLine = "";
    }

    // Set the line to be shown
    public void setLine(String line) {
        currentLine = line;
    }

    // Get the current dialogue line
    public String getLine() {
        return currentLine;
    }

    // Clear the dialogue
    public void clear() {
        currentLine = "";
    }
}