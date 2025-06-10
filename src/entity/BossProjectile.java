package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BossProjectile {
    public double x, y;
    public double vx, vy;
    public int width = 32, height = 32;
    public boolean active = true;
    public int damage = 30;
    private BufferedImage sprite;
    private static final double SPEED = 8.0;

    public BossProjectile(double startX, double startY, double targetX, double targetY) {
        this.x = startX;
        this.y = startY;
        double dx = targetX - startX;
        double dy = targetY - startY;
        double dist = Math.sqrt(dx*dx + dy*dy);
        this.vx = SPEED * dx / dist;
        this.vy = SPEED * dy / dist;
        try {
            sprite = ImageIO.read(new File("res/enemy/boss/proj.png"));
        } catch (IOException e) { sprite = null; }
    }

    public void update() {
        x += vx;
        y += vy;
    }

    public void draw(Graphics2D g2) {
        if (sprite != null) {
            g2.drawImage(sprite, (int)x, (int)y, width, height, null);
        } else {
            g2.setColor(Color.MAGENTA);
            g2.fillOval((int)x, (int)y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
} 