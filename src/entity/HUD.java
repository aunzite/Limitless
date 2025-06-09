package entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import main.KeyHandler;

public class HUD {

    // Attributes
    private int playerHealth;
    private int playerStamina;
    private String weaponName;
    private boolean showAttackHistory = false;
    
    // Stamina system
    private static final int MAX_STAMINA = 1000;  // Internal max stamina
    private static final int DISPLAY_MAX_STAMINA = 100;  // Display max stamina
    private static final int STAMINA_DRAIN_RATE = 5;  // Reduced from 10 to 5 (0.5% drain per frame)
    private static final int STAMINA_REGEN_RATE = 5;  // 0.5% regen per frame while idle
    private static final int STAMINA_WALK_REGEN_RATE = 3;  // 0.3% regen per frame while walking
    private boolean isInCooldown = false;
    private long lastStaminaDrainTime = 0;
    private static final int COOLDOWN_TIME = 500; // 0.5 second cooldown
    private boolean wasShiftPressed = false;  // Track previous frame's shift state
    private long lastShiftReleaseTime = 0;    // Track when shift was released
    private static final int SHIFT_COOLDOWN = 1000; // 1 second cooldown after releasing shift
    private long lastStaminaRegenTime = 0;    // Track when stamina regeneration can start
    private static final int REGEN_DELAY = 2000; // 2 second delay before stamina regeneration starts
    
    // UI Constants
    private static final int BAR_WIDTH = 400;
    private static final int BAR_HEIGHT = 40;
    private static final int BAR_PADDING = 10;
    private static final int CORNER_RADIUS = 20;
    private static final int FONT_SIZE = 28;
    
    // Fade effect variables
    private float controlHintsAlpha = 0f;
    private static final float FADE_SPEED = 0.02f;

    private KeyHandler keyH;  // Add KeyHandler reference

    // Constructor
    public HUD(KeyHandler keyH) {
        this.keyH = keyH;
        playerHealth = 100;
        playerStamina = MAX_STAMINA;  // Start with full stamina
        weaponName = "None";
        isInCooldown = false;
    }

    public void setKeyHandler(KeyHandler keyH) {
        this.keyH = keyH;
    }

    // Update method to sync player stats
    public void update(int playerHP, int playerStamina, String weaponName, boolean isMoving) {
        this.playerHealth = playerHP;
        this.playerStamina = playerStamina;
        this.weaponName = weaponName;
        
        // Update control hints fading
        if (!isMoving && controlHintsAlpha < 1f) {
            controlHintsAlpha = Math.min(1f, controlHintsAlpha + FADE_SPEED);
        } else if (isMoving && controlHintsAlpha > 0f) {
            controlHintsAlpha = Math.max(0f, controlHintsAlpha - FADE_SPEED);
        }
    }

    // Draw HUD on screen
    public void draw(Graphics2D g2, Weapon weapon) {
        // Save original composite
        AlphaComposite originalComposite = (AlphaComposite) g2.getComposite();
        BasicStroke originalStroke = (BasicStroke) g2.getStroke();
        
        // Draw health bar
        drawBar(g2, 10, 10, playerHealth, 100, Color.RED, "HP: " + playerHealth);
        
        // Draw stamina bar (convert internal stamina to display value)
        int displayStamina = (playerStamina * DISPLAY_MAX_STAMINA) / MAX_STAMINA;
        drawBar(g2, 10, 10 + BAR_HEIGHT + BAR_PADDING, displayStamina, DISPLAY_MAX_STAMINA, 
            new Color(0, 150, 255), "Stamina: " + displayStamina);
        
        // Draw weapon name
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        String displayWeapon = (weaponName == null || weaponName.equals("") || weaponName.equalsIgnoreCase("Steel Sword")) ? "None" : weaponName;
        g2.drawString("Weapon: " + displayWeapon, 20, 140);

        // Draw attack history if enabled
        if (showAttackHistory && weapon != null) {
            drawAttackHistory(g2, weapon);
        }
        
        // Restore original settings
        g2.setComposite(originalComposite);
        g2.setStroke(originalStroke);
    }

    private void drawBar(Graphics2D g2, int x, int y, int value, int maxValue, Color color, String label) {
        // Draw background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(x, y, BAR_WIDTH, BAR_HEIGHT, CORNER_RADIUS, CORNER_RADIUS);
        
        // Draw bar
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 180));
        int barWidth = (int)((value / (double)maxValue) * BAR_WIDTH);
        g2.fillRoundRect(x, y, barWidth, BAR_HEIGHT, CORNER_RADIUS, CORNER_RADIUS);
        
        // Draw border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(x, y, BAR_WIDTH, BAR_HEIGHT, CORNER_RADIUS, CORNER_RADIUS);
        
        // Draw label
        g2.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        g2.drawString(label, x + 10, y + 25);
    }

    private void drawAttackHistory(Graphics2D g2, Weapon weapon) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(10, 320, 400, 400, CORNER_RADIUS, CORNER_RADIUS);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        g2.drawString("Attack History:", 20, 320);
        
        g2.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE - 8));
        java.util.ArrayList<String> history = weapon.getAttackHistory();
        for (int i = 0; i < history.size(); i++) {
            g2.drawString((i+1) + ": " + history.get(i), 20, 310 + i*30);
        }
    }

    public void drainStamina() {
        if (playerStamina > 0) {
            playerStamina = Math.max(0, playerStamina - STAMINA_DRAIN_RATE);
            lastStaminaRegenTime = System.currentTimeMillis(); // Reset regen timer whenever stamina is drained
            if (playerStamina == 0) {
                isInCooldown = true;
                lastStaminaDrainTime = System.currentTimeMillis();
            }
        }
    }

    public void regenerateStamina(boolean isMoving) {
        // Handle shift key cooldown
        if (wasShiftPressed && !keyH.shiftPressed) {
            lastShiftReleaseTime = System.currentTimeMillis();
            lastStaminaRegenTime = System.currentTimeMillis(); // Reset regen timer when shift is released
        }
        wasShiftPressed = keyH.shiftPressed;

        // Handle stamina cooldown
        if (isInCooldown) {
            if (System.currentTimeMillis() - lastStaminaDrainTime >= COOLDOWN_TIME) {
                isInCooldown = false;
            } else {
                return;
            }
        }

        // Check if enough time has passed since last stamina drain to start regenerating
        if (System.currentTimeMillis() - lastStaminaRegenTime < REGEN_DELAY) {
            return;
        }

        // Regenerate stamina at different rates based on movement
        if (playerStamina < MAX_STAMINA) {
            int regenRate = isMoving ? STAMINA_WALK_REGEN_RATE : STAMINA_REGEN_RATE;
            playerStamina = Math.min(MAX_STAMINA, playerStamina + regenRate);
        }
    }

    public int getStamina() {
        return playerStamina;
    }

    public void setShowAttackHistory(boolean show) {
        this.showAttackHistory = show;
    }
    
    public boolean isShowAttackHistory() {
        return showAttackHistory;
    }

    public void setStamina(int stamina100) {
        this.playerStamina = Math.max(0, Math.min(1000, stamina100 * 10));
    }
}
