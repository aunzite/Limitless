package entity;

public class OBJ_Apple extends Item {
    public OBJ_Apple() {
        super("Apple", "res/object/apple.png", 1);
    }
    public OBJ_Apple(int quantity) {
        super("Apple", "res/object/apple.png", quantity);
    }
} 