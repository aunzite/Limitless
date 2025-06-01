package entity;
import java.util.ArrayList;

/////////////////////////////////////////////////////////////////////////////
// Limitless
// Dialogue.java
//
// Description: Stores and manages dialogue text for NPCs or story events. (Ahmed)
// - Holds and updates the current line of dialogue (Ahmed)
// - Stores a full history of all dialogue lines using an ArrayList (Ahmed)
// - Provides access to dialogue text (Ahmed)
// - Allows adding new lines dynamically via user input (Ahmed)
// - Clears or resets dialogue after interaction (Ahmed)
// - Returns dialogue text via toString (Ahmed)
/////////////////////////////////////////////////////////////////////////////

public class Dialogue {

    // Attribute (Ahmed)
    private String currentLine;  // Stores the current dialogue line to display
    private ArrayList<String> history;

    // Constructor (Ahmed)
    // Initializes dialogue with an empty string
    public Dialogue() {
        currentLine = "";
        history =  new ArrayList<String>();
    }

    // Accessor (Ahmed)
    // Returns the current dialogue line
    public String getLine() {
        return currentLine;
    }

    // Accessor (Ahmed)
    //Returns the history of Dialogue lines
    public ArrayList<String> getHistory(){
        return history;
    }

    // Mutator (Ahmed)
    // Sets the current dialogue line to a new message
    public void setLine(String line) {
        currentLine = line;
        history.add(line);
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
