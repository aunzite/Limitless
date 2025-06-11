package entity;

//imports
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class BossNoxar {

    //variables
    //position
    public int x, y; 
    public int width = 48;
    public int height = 64;

    //health
    public int health = 200;
    public int maxHealth = 200;

    //name
    public String name = "Noxar";

    //states
    public boolean isDead = false;
    // Animation state
    public static final String STATE_WALK = "WALK";
    public static final String STATE_SPELLCAST = "SPELLCAST";
    public static final String STATE_DYING = "DYING";
    public String state = STATE_WALK;

    //animation
    public long lastAttackTime = 0;
    public int walkFrame = 0;
    public int castFrame = 0;
    public int hurtFrame = 0;
    public long lastFrameTime = 0;

    //sprites
    public BufferedImage[][] walkSprites;
    public BufferedImage[][] spellcastSprites;
    public BufferedImage[] hurtSprites;

    //animation variables
    public int frameDuration = 180; // in ms
    public int castDuration = 1000; // in ms
    public int hurtDuration = 1200; // in ms

    //player position
    private int playerX = 0;
    private int playerY = 0;

    //pacing logic
    private int paceStartX;
    private int paceEndX;
    private int paceSpeed = 2;
    private int paceDir = 1; // + = right, - = left
    private boolean initializedPace = false;

    //animation counters
    private int spriteCounter = 0;
    private int spriteNum = 0; // 0-8 for 9 frames

    //animation speed
    private static final int WALK_FRAME_COUNT = 9; 
    private static final int WALK_FRAME_SPEED = 12; // frames per update

    //direction
    public String direction = "right";

    //constructor
    public BossNoxar(int x, int y) {
        this.x = x;
        this.y = y;
        loadSprites();
    }

    //load sprites
    private void loadSprites() {

        // Load walk.png grid (4 rows x 9 cols)
        walkSprites = new BufferedImage[4][9];

        try {
            BufferedImage walkSheet = ImageIO.read(new File("res/enemy/boss/walk.png"));

            //get sheet width and height
            int sheetWidth = walkSheet.getWidth();
            int sheetHeight = walkSheet.getHeight();

            //get gap sizes
            int gapTop = 15, gapBottom = 2, gapLeft = 17, gapRight = 13;

            //get frame width and height
            int frameW = (sheetWidth - gapLeft - gapRight) / 9;
            int frameH = (sheetHeight - gapTop - gapBottom) / 4;

            //loop through rows and cols
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 9; col++) {

                    //get frame x and y
                    int x = gapLeft + col * frameW;
                    int y = gapTop + row * frameH;

                    //get frame
                    BufferedImage raw = walkSheet.getSubimage(x, y, frameW, frameH);

                    //pad frame
                    walkSprites[row][col] = raw;
                }
            }

        } catch (IOException e) { 

            //if error, set walkSprites to null
            walkSprites = null; 
        }

        // Load spellcast.png (4 rows x 8 cols)
        spellcastSprites = new BufferedImage[4][8];

        try {
            BufferedImage castSheet = ImageIO.read(new File("res/enemy/boss/spellcast.png"));

            //get cast data
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

            //loop through rows and cols
            int index = 0;
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 8; col++) {

                    //get gap top, left, width, and height
                    int gapTop = castData[index][0];
                    int gapLeft = castData[index][1];
                    int w = castData[index][2];
                    int h = castData[index][3];

                    //get frame
                    BufferedImage raw = castSheet.getSubimage(gapLeft, gapTop + row*64, w, h);

                    //set frame
                    spellcastSprites[row][col] = raw;

                    //increment index
                    index++;
                }
            }

        } catch (IOException e) { 

            //if error, set spellcastSprites to null
            spellcastSprites = null; 
        }

        // Load hurt.png (6 frames horizontally)
        try {
            BufferedImage hurtSheet = ImageIO.read(new File("res/enemy/boss/hurt.png"));
            hurtSprites = new BufferedImage[6];

            //get frame width and height
            int frameW = hurtSheet.getWidth() / 6;
            int frameH = hurtSheet.getHeight();

            //loop through frames
            for (int i = 0; i < 6; i++) {
                hurtSprites[i] = hurtSheet.getSubimage(i * frameW, 0, frameW, frameH);
            }
        } catch (IOException e) { 

            //if error, set hurtSprites to null
            hurtSprites = null; 
        }
    }

    //update
    public void update() {

        //get current time
        long now = System.currentTimeMillis();

        //if dead, return
        if (isDead) return;

        // Handle dying state
        if (state.equals(STATE_DYING)) {

            //if time since last frame is greater than frame duration
            if (now - lastFrameTime > frameDuration) {

                //increment hurt frame
                hurtFrame++;

                //set last frame time
                lastFrameTime = now;

                //if hurt frame is greater than hurtSprites length
                if (hurtFrame >= hurtSprites.length) {

                    //set hurt frame to last frame
                    hurtFrame = hurtSprites.length - 1;

                    //set isDead to true
                    isDead = true;
                }
            }
            return;
        }

        // Handle spellcast state
        if (state.equals(STATE_SPELLCAST)) {

            //if time since last frame is greater than frame duration
            if (now - lastFrameTime > frameDuration) {

                //increment cast frame
                castFrame++;

                //set last frame time
                lastFrameTime = now;

                //if cast frame is greater than spellcastSprites length
                if (castFrame >= 8) {
                    castFrame = 0;
                    state = STATE_WALK;
                }
            }
            return;
        }
        // Handle walk state
        if (state.equals(STATE_WALK)) {

            // Start spellcast every 5 seconds
            if (now - lastAttackTime > 5000) {
                state = STATE_SPELLCAST;
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

            //if x is less than paceStartX
            if (x < paceStartX) {
                x = paceStartX;
                paceDir = 1;
                
            } else if (x > paceEndX) {
                x = paceEndX;
                paceDir = -1;
            }

            // Walk animation
            spriteCounter++;

            //if sprite counter is greater than walk frame speed
            if (spriteCounter > WALK_FRAME_SPEED) {

                //increment sprite num
                spriteNum = (spriteNum + 1) % WALK_FRAME_COUNT;

                //reset sprite counter
                spriteCounter = 0;
            }
        }
    }

    //take damage
    public void takeDamage(int dmg) {

        //if dead, return
        if (isDead) return;

        //subtract damage from health
        health -= dmg;
        if (health <= 0) {
            health = 0;
            state = STATE_DYING;
            hurtFrame = 0;
            lastFrameTime = System.currentTimeMillis();
        }
    }

    //setters

    //set player position
    public void setPlayerPosition(int px, int py) {
        this.playerX = px;
        this.playerY = py;
    }

    //draw
    public void draw(Graphics2D g2, main.GamePanel gp) {
        BufferedImage sprite = null;

        //set walk row
        int walkRow = 3; // Default to right

        //if state is walk
        if (state.equals(STATE_WALK)) {

            //if paceDir is -1, set walkRow to 1, else set to 3
            if (paceDir == -1) {
                walkRow = 1;
            } else {
                walkRow = 3;
            }

            //set sprite
            sprite = walkSprites[walkRow][spriteNum];

        //if state is spellcast
        } else if (state.equals(STATE_SPELLCAST)) {

            // Face player for spellcast, but use walk animation

            //get dx and dy
            int dx = playerX - (x + width/2);
            int dy = playerY - (y + height/2);

            //if dx is greater than dy, set walkRow to 1 or 3
            if (Math.abs(dx) > Math.abs(dy)) {

                //if dx is less than 0, set walkRow to 1, else set to 3
                if (dx < 0) {
                    walkRow = 1;
                } else {
                    walkRow = 3;
                }
            } else {

                //if dy is less than 0, set walkRow to 0, else set to 2
                if (dy < 0) {
                    walkRow = 0;
                } else {
                    walkRow = 2;
                }
            }

            //set sprite
            sprite = walkSprites[walkRow][spriteNum];

        //if state is dying
        } else if (state.equals(STATE_DYING)) {

            //if hurtSprites is not null
            if (hurtSprites != null) {
                sprite = hurtSprites[hurtFrame];
            }
        }

        //if sprite is not null
        if (sprite != null) {

            //set scale
            double scale = 1.3;

            //get draw height and width
            int drawHeight = (int)(gp.tileSize * scale);
            int drawWidth = (int)(sprite.getWidth() * (drawHeight / (double)sprite.getHeight()));

            //get feet x and y
            int feetX = x + gp.tileSize / 2;
            int feetY = y + gp.tileSize;

            //get draw x and y
            int drawX = feetX - drawWidth / 2;
            int drawY = feetY - drawHeight;

            //draw sprite
            g2.drawImage(sprite, drawX, drawY, drawWidth, drawHeight, null);
        }

        // Draw name above
        g2.setFont(new Font("Arial", Font.BOLD, 24));

        //set color
        g2.setColor(Color.WHITE);

        //get name width
        int nameWidth = g2.getFontMetrics().stringWidth(name);

        //draw name
        g2.drawString(name, x + gp.tileSize / 2 - nameWidth / 2, y - 10);
    }

    //getters

    //get bounds
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
} 