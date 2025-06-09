package object;

import javax.imageio.ImageIO;
import java.io.File;

public class OBJ_Solthorn extends SuperObject {
    public OBJ_Solthorn() {
        name = "Solthorn";
        try {
            image = ImageIO.read(new File("res/object/solthorn.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return "Solthorn\nA legendary blade passed down through Elaria's bloodline, forged around a gem said to hold unimaginable power.";
    }
} 