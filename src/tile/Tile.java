/////////////////////////////////////////////////////////////////////////////
// Limitless
// Tile.java
// 
// Description: Base tile class including:
// - Tile properties
// - Collision handling
// - Tile rendering
// - Tile types
/////////////////////////////////////////////////////////////////////////////

package tile;

import java.awt.image.BufferedImage;

// Base class for all game tiles
public class Tile {
    // Tile properties
    public BufferedImage image;     // Tile sprite
    public boolean collision = false;  // Collision flag
    public int type;                // Tile type identifier
    
    // Tile types
    public static final int TYPE_NORMAL = 0;    // Regular walkable tile
    public static final int TYPE_SOLID = 1;     // Solid collision tile
    public static final int TYPE_WATER = 2;     // Water tile
    public static final int TYPE_GRASS = 3;     // Grass tile
    public static final int TYPE_DECOR = 4;     // Decorative tile

    // Default constructor creates a basic tile with no collision
    public Tile() {
        this.image = null;
        this.collision = false;
    }
}