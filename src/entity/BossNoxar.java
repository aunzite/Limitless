package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BossNoxar {
    public int x, y; // Position on the shrine platform
    public int width = 48, height = 64; // Default size
    public int health = 200;
    public int maxHealth = 200;
    public String name = "Noxar";
    public boolean isDead = false;
    public boolean isCasting = false;
    public boolean isDying = false;
    public long lastAttackTime = 0;
    public int walkFrame = 0;
    public int castFrame = 0;
    public int hurtFrame = 0;
    public long lastFrameTime = 0;
    public BufferedImage[][] walkSprites;
    public BufferedImage[][] spellcastSprites; // 4 rows x 8 cols
    public BufferedImage[] hurtSprites;
    public int walkRows = 4, walkCols = 9;
    public int frameDuration = 180; // ms per frame (slowed down for clarity)
    public int castDuration = 1000; // ms for spellcast
    public int hurtDuration = 1200; // ms for death
    private int playerX = 0, playerY = 0;
    // Pacing logic
    private int paceStartX, paceEndX;
    private int paceSpeed = 2;
    private int paceDir = 1; // 1 = right, -1 = left
    private boolean initializedPace = false;
    private static final int PAD_WIDTH = 64;
    private static final int PAD_HEIGHT = 64;

    // Animation state
    private enum State { WALK, SPELLCAST, DYING }
    private State state = State.WALK;
    // Animation counters like player
    private int spriteCounter = 0;
    private int spriteNum = 0; // 0-8 for 9 frames
    private static final int WALK_FRAME_COUNT = 9;
    private static final int WALK_FRAME_SPEED = 12; // frames per update (like player)

    public String direction = "right";

    public BossNoxar(int x, int y) {
        this.x = x;
        this.y = y;
        loadSprites();
    }

    private void loadSprites() {
        // Load walk.png grid (4 rows x 9 cols, uniform gaps)
        walkSprites = new BufferedImage[walkRows][walkCols];
        try {
            BufferedImage walkSheet = ImageIO.read(new File("res/enemy/boss/walk.png"));
            int sheetWidth = walkSheet.getWidth();
            int sheetHeight = walkSheet.getHeight();
            int gapTop = 15, gapBottom = 2, gapLeft = 17, gapRight = 13;
            int frameW = (sheetWidth - gapLeft - gapRight) / walkCols;
            int frameH = (sheetHeight - gapTop - gapBottom) / walkRows;
            for (int row = 0; row < walkRows; row++) {
                for (int col = 0; col < walkCols; col++) {
                    int x = gapLeft + col * frameW;
                    int y = gapTop + row * frameH;
                    BufferedImage raw = walkSheet.getSubimage(x, y, frameW, frameH);
                    walkSprites[row][col] = padFrame(raw);
                }
            }
        } catch (IOException e) { walkSprites = null; }
        // Load spellcast.png (4 rows x 8 cols, custom gaps)
        spellcastSprites = new BufferedImage[4][8];
        try {
            BufferedImage castSheet = ImageIO.read(new File("res/enemy/boss/spellcast.png"));
            int[][] castData = new int[][] {
                // Row 1
                {16,17,30,46},{16,26,28,46},{16,34,22,46},{16,0,56,46},{16,0,56,46},{16,0,56,46},{16,0,40,46},{16,6,36,46},
                // Row 2
                {14,21,23,48},{14,29,23,48},{14,37,19,48},{14,0,56,48},{14,0,56,48},{14,0,27,48},{14,2,33,48},{14,12,28,48},
                // Row 3
                {15,17,30,47},{15,25,30,47},{15,34,22,47},{15,0,56,47},{15,0,56,47},{15,0,56,47},{15,0,40,47},{15,7,34,47},
                // Row 4
                {14,20,23,48},{14,28,23,48},{14,36,20,48},{14,0,56,48},{14,0,56,48},{14,0,56,48},{14,0,30,48},{14,8,28,48}
            };
            int idx = 0;
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 8; col++) {
                    int gapTop = castData[idx][0];
                    int gapLeft = castData[idx][1];
                    int w = castData[idx][2];
                    int h = castData[idx][3];
                    BufferedImage raw = castSheet.getSubimage(gapLeft, gapTop + row*64, w, h);
                    spellcastSprites[row][col] = padFrame(raw);
                    idx++;
                }
            }
        } catch (IOException e) { spellcastSprites = null; }
        // Load hurt.png (assume 6 frames horizontally)
        try {
            BufferedImage hurtSheet = ImageIO.read(new File("res/enemy/boss/hurt.png"));
            hurtSprites = new BufferedImage[6];
            int frameW = hurtSheet.getWidth() / 6;
            int frameH = hurtSheet.getHeight();
            for (int i = 0; i < 6; i++) {
                hurtSprites[i] = hurtSheet.getSubimage(i * frameW, 0, frameW, frameH);
            }
        } catch (IOException e) { hurtSprites = null; }
    }

    // Helper to pad a sprite to fixed size, centering horizontally and aligning feet to bottom
    private BufferedImage padFrame(BufferedImage src) {
        BufferedImage padded = new BufferedImage(PAD_WIDTH, PAD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = padded.createGraphics();
        int x = (PAD_WIDTH - src.getWidth()) / 2;
        int y = PAD_HEIGHT - src.getHeight();
        g.drawImage(src, x, y, null);
        g.dispose();
        return padded;
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (isDead) return;
        // Handle dying state
        if (isDying) {
            state = State.DYING;
            isCasting = false;
            if (now - lastFrameTime > frameDuration) {
                hurtFrame++;
                lastFrameTime = now;
                if (hurtFrame >= hurtSprites.length) {
                    hurtFrame = hurtSprites.length - 1;
                    isDead = true;
                }
            }
            return;
        }
        // Handle spellcast state
        if (state == State.SPELLCAST) {
            isCasting = true;
            if (now - lastFrameTime > frameDuration) {
                castFrame++;
                lastFrameTime = now;
                if (castFrame >= 8) {
                    castFrame = 0;
                    state = State.WALK;
                    isCasting = false;
                }
            }
            return;
        }
        isCasting = false;
        // Handle walk state
        if (state == State.WALK) {
            // Start spellcast every 5 seconds
            if (now - lastAttackTime > 5000) {
                state = State.SPELLCAST;
                castFrame = 0;
                lastAttackTime = now;
                return;
            }
            // Initialize pacing range on first update
            if (!initializedPace) {
                paceStartX = x - 2 * 48; // 2 tiles left
                paceEndX = x + 2 * 48;   // 2 tiles right
                initializedPace = true;
            }
            // Pace left/right
            x += paceSpeed * paceDir;
            if (x < paceStartX) {
                x = paceStartX;
                paceDir = 1;
            } else if (x > paceEndX) {
                x = paceEndX;
                paceDir = -1;
            }
            // Walk animation (like player)
            spriteCounter++;
            if (spriteCounter > WALK_FRAME_SPEED) {
                spriteNum = (spriteNum + 1) % WALK_FRAME_COUNT;
                spriteCounter = 0;
            }
        }
    }

    public void takeDamage(int dmg) {
        if (isDead || isDying) return;
        health -= dmg;
        if (health <= 0) {
            health = 0;
            isDying = true;
            hurtFrame = 0;
            lastFrameTime = System.currentTimeMillis();
        }
    }

    public void setPlayerPosition(int px, int py) {
        this.playerX = px;
        this.playerY = py;
    }

    public void draw(Graphics2D g2, main.GamePanel gp) {
        BufferedImage sprite = null;
        int walkRow = 3; // Default to right
        if (state == State.WALK) {
            walkRow = (paceDir == -1) ? 1 : 3;
            sprite = walkSprites[walkRow][spriteNum];
        } else if (state == State.SPELLCAST) {
            // Face player for spellcast, but use walk animation
            int dx = playerX - (x + width/2);
            int dy = playerY - (y + height/2);
            if (Math.abs(dx) > Math.abs(dy)) {
                walkRow = (dx < 0) ? 1 : 3;
            } else {
                walkRow = (dy < 0) ? 0 : 2;
            }
            sprite = walkSprites[walkRow][spriteNum];
        } else if (state == State.DYING) {
            if (hurtSprites != null) {
                sprite = hurtSprites[hurtFrame];
            }
        }
        if (sprite != null) {
            double scale = 1.3;
            int drawHeight = (int)(gp.tileSize * scale);
            int drawWidth = (int)(sprite.getWidth() * (drawHeight / (double)sprite.getHeight()));
            int feetX = x + gp.tileSize / 2;
            int feetY = y + gp.tileSize; // logical feet position
            int drawX = feetX - drawWidth / 2;
            int drawY = feetY - drawHeight;
            g2.drawImage(sprite, drawX, drawY, drawWidth, drawHeight, null);
        }
        // Draw name above
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        int nameWidth = g2.getFontMetrics().stringWidth(name);
        g2.drawString(name, x + gp.tileSize / 2 - nameWidth / 2, y - 10);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
} 