package entity;
import java.util.ArrayList;

/////////////////////////////////////////////////////////////////////////////
// Limitless
// Dialogue.java
//
// Description: Stores and manages dialogue text for NPCs or story events. 
// - Holds and updates the current line of dialogue 
// - Stores a full history of all dialogue lines using an ArrayList 
// - Provides access to dialogue text 
// - Allows adding new lines dynamically via user input 
// - Clears or resets dialogue after interaction 
// - Returns dialogue text via toString 
/////////////////////////////////////////////////////////////////////////////

public class Dialogue {

    // Attribute 
    private String currentLine;  // Stores the current dialogue line to display
    private ArrayList<String> history;

    // Constructor 
    // Initializes dialogue with an empty string
    public Dialogue() {
        currentLine = "";
        history =  new ArrayList<String>();
    }

    // Accessor 
    // Returns the current dialogue line
    public String getLine() {
        return currentLine;
    }

    // Accessor 
    //Returns the history of Dialogue lines
    public ArrayList<String> getHistory(){
        return history;
    }

    // Mutator 
    // Sets the current dialogue line to a new message
    public void setLine(String line) {
        currentLine = line;
        history.add(line);
    }

    // Mutator
    // Clears the current dialogue line
    public void clear() {
        currentLine = "";
    }

    // toString method 
    // Returns the current dialogue as a string
    public String toString() {
        return currentLine;
    }
}
