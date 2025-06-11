package entity;

//imports
import main.*;

public class OBJ_Apple extends Item {

    //constructor
    public OBJ_Apple() {

        //call super constructor
        super("Apple", "res/object/apple.png", 1);
    }

    public OBJ_Apple(int quantity) {
        super("Apple", "res/object/apple.png", quantity);
    }
} 