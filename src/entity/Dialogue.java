package entity;

/////////////////////////////////////////////////////////////////////////////
// Limitless
// Dialogue.java
//
// Description: Stores and manages dialogue text for NPCs or story events. (Ahmed)
// - Holds and updates the current line of dialogue (Ahmed)
// - Provides access to dialogue text (Ahmed)
// - Clears or resets dialogue after interaction (Ahmed)
// - Returns dialogue text via toString (Ahmed)
/////////////////////////////////////////////////////////////////////////////

public class Dialogue {

    // Attribute (Ahmed)
    private String currentLine;  // Stores the current dialogue line to display

    // Constructor (Ahmed)
    // Initializes dialogue with an empty string
    public Dialogue() {
        currentLine = "";
    }

    // Accessor (Ahmed)
    // Returns the current dialogue line
    public String getLine() {
        return currentLine;
    }

    // Mutator (Ahmed)
    // Sets the current dialogue line to a new message
    public void setLine(String line) {
        currentLine = line;
    }

    // Mutator (Ahmed)
    // Clears the current dialogue line
    public void clear() {
        currentLine = "";
    }

    // toString method (Ahmed)
    // Returns the current dialogue as a string
    public String toString() {
        return currentLine;
    }
}
