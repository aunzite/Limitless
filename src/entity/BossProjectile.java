package entity;

//imports
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class BossProjectile {

    //variables
    //position
    public double x, y; //position
    public double vx, vy; //velocity
    public int width = 32;
    public int height = 32;

    //states
    public boolean active = true;

    //damage
    public int damage = 30;

    //sprite
    private BufferedImage sprite;

    //speed
    private static final double SPEED = 8.0;

    //constructor
    public BossProjectile(double startX, double startY, double targetX, double targetY) {
        //set position
        this.x = startX;
        this.y = startY;

        //get direction
        double dx = targetX - startX;
        double dy = targetY - startY;

        //get distance
        double dist = Math.sqrt(dx*dx + dy*dy);

        //set velocity
        this.vx = SPEED * dx / dist;
        this.vy = SPEED * dy / dist;

        //load sprite
        try {
            sprite = ImageIO.read(new File("res/enemy/boss/proj.png"));
        } catch (IOException e) { 

            //if error, set sprite to null
            sprite = null; 
        }
    }

    //update
    public void update() {
        //update position
        x += vx;
        y += vy;
    }

    //draw
    public void draw(Graphics2D g2) {
        //if sprite is not null
        if (sprite != null) {
            //draw sprite
            g2.drawImage(sprite, (int)x, (int)y, width, height, null);
        } else {
            //set color
            g2.setColor(Color.MAGENTA);
            //draw circle
            g2.fillOval((int)x, (int)y, width, height);
        }
    }

    //getters

    //get bounds
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
} 