package tile;

import java.awt.image.BufferedImage;

// Represents a single tile in the game world
// Used for building the game map and handling tile-based collisions
public class Tile {
    
    // The visual representation of the tile
    // Loaded from an image file and rendered to the screen
    public BufferedImage image;

    // Determines if entities can pass through this tile
    // true = solid/blocked, false = passable
    public boolean collision = false;

    // Default constructor creates a basic tile with no collision
    public Tile() {
        this.image = null;
        this.collision = false;
    }
}