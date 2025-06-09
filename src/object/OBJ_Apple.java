package object;

import javax.imageio.ImageIO;
import java.io.File;

public class OBJ_Apple extends SuperObject {
    public int quantity = 1;

    public OBJ_Apple() {
        this(1);
    }
    public OBJ_Apple(int quantity) {
        name = "Apple";
        this.quantity = quantity;
        try {
            image = ImageIO.read(new File("res/object/apple.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        String desc = "A fresh, juicy apple that restores your vitality.\nEffect: Restores 20 health and 15 stamina.";
        if (quantity > 1) desc += "\nQuantity: " + quantity;
        return desc;
    }

    public boolean isNear(OBJ_Apple other, int hitboxSize) {
        int dx = this.worldX - other.worldX;
        int dy = this.worldY - other.worldY;
        return Math.abs(dx) < hitboxSize && Math.abs(dy) < hitboxSize;
    }
} 