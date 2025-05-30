package entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class HUD {

    // Attributes (Ahmed)
    private int playerHealth;
    private int playerStamina;
    private String weaponName;

    // Constructor (Ahmed)
    public HUD() {
        playerHealth = 100;
        playerStamina = 100;
        weaponName = "None";
    }

    // Update method to sync player stats (Ahmed)
    public void update(int hp, int stamina, String weapon) {
        playerHealth = hp;
        playerStamina = stamina;
        weaponName = weapon;
    }

    // Draw HUD on screen (Ahmed)
    public void draw(Graphics2D g2) {
        // Draw health bar
        g2.setColor(Color.RED);
        g2.fillRect(10, 10, playerHealth * 2, 20);

        // Draw stamina bar
        g2.setColor(Color.BLUE);
        g2.fillRect(10, 40, playerStamina * 2, 20);

        // Draw weapon name
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString("Weapon: " + weaponName, 10, 80);
    }
}
