package entity;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Item {
    private String name;
    private BufferedImage image;
    private int x, y; // Position in inventory grid
    private boolean isDragging = false;
    private int dragOffsetX, dragOffsetY;
    private int quantity = 1;

    public Item(String name, String imagePath) {
        this(name, imagePath, 1);
    }

    public Item(String name, String imagePath, int quantity) {
        this.name = name;
        this.quantity = quantity;
        loadImage(imagePath);
    }

    private void loadImage(String path) {
        try {
            image = ImageIO.read(new File(path));
        } catch (Exception e) {
            System.err.println("Error loading item image: " + e.getMessage());
        }
    }

    public String getName() {
        return name;
    }
    
    public BufferedImage getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public int getDragOffsetX() {
        return dragOffsetX;
    }

    public void setDragOffsetX(int dragOffsetX) {
        this.dragOffsetX = dragOffsetX;
    }

    public int getDragOffsetY() {
        return dragOffsetY;
    }

    public void setDragOffsetY(int dragOffsetY) {
        this.dragOffsetY = dragOffsetY;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int q) {
        quantity = q;
    }
} 