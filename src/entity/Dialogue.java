package entity;

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

//imports
import java.util.ArrayList;

public class Dialogue {

    //variables
    //current line
    private String currentLine;

    //history
    private ArrayList<String> history;

    //constructor
    public Dialogue() {
        //set current line to empty
        currentLine = "";
        //create new history
        history = new ArrayList<String>();
    }

    //getters

    //get current line
    public String getLine() {
        return currentLine;
    }

    //get history
    public ArrayList<String> getHistory() {
        return history;
    }

    //setters

    //set current line
    public void setLine(String line) {
        //set current line
        currentLine = line;
        //add line to history
        history.add(line);
    }

    //clear current line
    public void clear() {
        //set current line to empty
        currentLine = "";
    }

    //get string
    public String toString() {
        return currentLine;
    }
}
