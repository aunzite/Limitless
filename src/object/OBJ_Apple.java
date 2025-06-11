/////////////////////////////////////////////////////////////////////////////
// Limitless
// OBJ_Apple.java
// 
// Description: Apple item object including:
// - Health restoration
// - Stackable items
// - Item pickup
// - Item usage
/////////////////////////////////////////////////////////////////////////////

package object;

import javax.imageio.ImageIO;
import java.io.File;

// Apple item that can be collected and consumed
public class OBJ_Apple extends SuperObject {
    // Item properties
    public int quantity = 1;        // Number of apples in stack

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

    // Get quantity of apples in stack
    public int getQuantity() {
        return quantity;
    }

    // Set quantity of apples in stack
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
} 