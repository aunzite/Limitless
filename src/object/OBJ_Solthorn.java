/////////////////////////////////////////////////////////////////////////////
// Limitless
// OBJ_Solthorn.java
// 
// Description: Legendary weapon object including:
// - Special abilities
// - Unique properties
// - Weapon stats
// - Special effects
/////////////////////////////////////////////////////////////////////////////

package object;

import javax.imageio.ImageIO;
import java.io.File;

// Legendary weapon with special abilities
public class OBJ_Solthorn extends SuperObject {
    // Constructor
    public OBJ_Solthorn() {
        name = "Solthorn";
        try {
            image = ImageIO.read(new File("res/object/solthorn.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get weapon description
    public String getDescription() {
        return "Solthorn\nA legendary blade passed down through Elaria's bloodline, forged around a gem said to hold unimaginable power.";
    }
} 