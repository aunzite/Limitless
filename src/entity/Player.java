package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;

    public Player (GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;

        setDefaultValues();
        getPlayerImage();
    }
    public void setDefaultValues (){

        x = gp.screenWidth/2 - (gp.tileSize/2); // Center the player on the screen
        y = gp.screenHeight/2 - (gp.tileSize/2); // Center the player on the screen
        speed = 2;
        direction = "down";
    }
    private BufferedImage getSprite(String sheetPath, int row, int col, int spriteWidth, int spriteHeight) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(sheetPath));
            return spriteSheet.getSubimage(
                (col * spriteWidth + 9) + (col*25),  // x coordinate
                (row * spriteHeight + 14) + (row*13), // y coordinate
                spriteWidth,        // sprite width
                spriteHeight        // sprite height
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void getPlayerImage() {
        try {
            String spriteSheetPath = "res/player/walk.png";
            int spriteWidth = 39;  // adjust based on your sprite size
            int spriteHeight = 50; // adjust based on your sprite size
            
            // Example: Load sprites from specific rows and columns
            // Parameters: (sheetPath, row, column, width, height)
            up1 = getSprite(spriteSheetPath, 0, 0, spriteWidth, spriteHeight);    // First row, first column
            up2 = getSprite(spriteSheetPath, 0, 1, spriteWidth, spriteHeight);    // First row, second column
            up3 = getSprite(spriteSheetPath, 0, 2, spriteWidth, spriteHeight);    // First row, third column
            up4 = getSprite(spriteSheetPath, 0, 3, spriteWidth, spriteHeight);    // First row, fourth column
            up5 = getSprite(spriteSheetPath, 0, 4, spriteWidth, spriteHeight);    // First row, fifth column
            up6 = getSprite(spriteSheetPath, 0, 5, spriteWidth, spriteHeight);    // First row, sixth column
            up7 = getSprite(spriteSheetPath, 0, 6, spriteWidth, spriteHeight);    // First row, seventh column
            up8 = getSprite(spriteSheetPath, 0, 7, spriteWidth, spriteHeight);    // First row, eighth column
            up9 = getSprite(spriteSheetPath, 0, 8, spriteWidth, spriteHeight);    // First row, ninth column
            
            left1 = getSprite(spriteSheetPath, 1, 0, spriteWidth, spriteHeight);    // Second row, first column
            left2 = getSprite(spriteSheetPath, 1, 1, spriteWidth, spriteHeight);    // Second row, second column
            left3 = getSprite(spriteSheetPath, 1, 2, spriteWidth, spriteHeight);    // Second row, third column
            left4 = getSprite(spriteSheetPath, 1, 3, spriteWidth, spriteHeight);    // Second row, fourth column
            left5 = getSprite(spriteSheetPath, 1, 4, spriteWidth, spriteHeight);    // Second row, fifth column
            left6 = getSprite(spriteSheetPath, 1, 5, spriteWidth, spriteHeight);    // Second row, sixth column
            left7 = getSprite(spriteSheetPath, 1, 6, spriteWidth, spriteHeight);    // Second row, seventh column
            left8 = getSprite(spriteSheetPath, 1, 7, spriteWidth, spriteHeight);    // Second row, eighth column
            left9 = getSprite(spriteSheetPath, 1, 8, spriteWidth, spriteHeight);    // Second row, ninth column

            down1 = getSprite(spriteSheetPath, 2, 0, spriteWidth, spriteHeight);    // Third row, first column
            down2 = getSprite(spriteSheetPath, 2, 1, spriteWidth, spriteHeight);    // Third row, second column
            down3 = getSprite(spriteSheetPath, 2, 2, spriteWidth, spriteHeight);    // Third row, third column
            down4 = getSprite(spriteSheetPath, 2, 3, spriteWidth, spriteHeight);    // Third row, fourth column
            down5 = getSprite(spriteSheetPath, 2, 4, spriteWidth, spriteHeight);    // Third row, fifth column
            down6 = getSprite(spriteSheetPath, 2, 5, spriteWidth, spriteHeight);    // Third row, sixth column
            down7 = getSprite(spriteSheetPath, 2, 6, spriteWidth, spriteHeight);    // Third row, seventh column
            down8 = getSprite(spriteSheetPath, 2, 7, spriteWidth, spriteHeight);    // Third row, eighth column
            down9 = getSprite(spriteSheetPath, 2, 8, spriteWidth, spriteHeight);    // Third row, ninth column
            
            right1 = getSprite(spriteSheetPath, 3, 0, spriteWidth, spriteHeight);    // Fourth row, first column
            right2 = getSprite(spriteSheetPath, 3, 1, spriteWidth, spriteHeight);    // Fourth row, second column
            right3 = getSprite(spriteSheetPath, 3, 2, spriteWidth, spriteHeight);    // Fourth row, third column
            right4 = getSprite(spriteSheetPath, 3, 3, spriteWidth, spriteHeight);    // Fourth row, fourth column
            right5 = getSprite(spriteSheetPath, 3, 4, spriteWidth, spriteHeight);    // Fourth row, fifth column
            right6 = getSprite(spriteSheetPath, 3, 5, spriteWidth, spriteHeight);    // Fourth row, sixth column
            right7 = getSprite(spriteSheetPath, 3, 6, spriteWidth, spriteHeight);    // Fourth row, seventh column
            right8 = getSprite(spriteSheetPath, 3, 7, spriteWidth, spriteHeight);    // Fourth row, eighth column
            right9 = getSprite(spriteSheetPath, 3, 8, spriteWidth, spriteHeight);    // Fourth row, ninth column
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void update(){

        if(keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true){
            
            if(keyH.upPressed == true){
                direction = "up";
                y -= speed;
            }
            if(keyH.downPressed == true){
                direction = "down";
                y += speed;
            }
            if(keyH.leftPressed == true){
                direction = "left";
                x -= speed;
            }
            if(keyH.rightPressed == true){
                direction = "right";
                x += speed;
            }
            if (keyH.shiftPressed == true){
                speed = 4;
            }
            else{
                speed = 2;
            }
    
            spriteCounter++;
            if(spriteCounter > 12){
                switch (spriteNum){
                    case 1 -> spriteNum = 2;
                    case 2 -> spriteNum = 3;
                    case 3 -> spriteNum = 4;
                    case 4 -> spriteNum = 5;
                    case 5 -> spriteNum = 6;
                    case 6 -> spriteNum = 7;
                    case 7 -> spriteNum = 8;
                    case 8 -> spriteNum = 9;
                    case 9 -> spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }
    }
    
    public void draw(Graphics2D g2){
        

        BufferedImage image = null;
        switch(direction){
            case "up" -> {
                switch (spriteNum){
                    case 1 -> image = up1;
                    case 2 -> image = up2;
                    case 3 -> image = up3;
                    case 4 -> image = up4;
                    case 5 -> image = up5;
                    case 6 -> image = up6;
                    case 7 -> image = up7;
                    case 8 -> image = up8;
                    case 9 -> image = up9;
                }
            }

            case "down" -> {
                switch (spriteNum){
                    case 1 -> image = down1;
                    case 2 -> image = down2;
                    case 3 -> image = down3;
                    case 4 -> image = down4;
                    case 5 -> image = down5;
                    case 6 -> image = down6;
                    case 7 -> image = down7;
                    case 8 -> image = down8;
                    case 9 -> image = down9;
                }
            }

            case "left" -> {
                switch (spriteNum){
                    case 1 -> image = left1;
                    case 2 -> image = left2;
                    case 3 -> image = left3;
                    case 4 -> image = left4;
                    case 5 -> image = left5;
                    case 6 -> image = left6;
                    case 7 -> image = left7;
                    case 8 -> image = left8;
                    case 9 -> image = left9;
                }
            }

            case "right" -> {
                switch (spriteNum){
                    case 1 -> image = right1;
                    case 2 -> image = right2;
                    case 3 -> image = right3;
                    case 4 -> image = right4;
                    case 5 -> image = right5;
                    case 6 -> image = right6;
                    case 7 -> image = right7;
                    case 8 -> image = right8;
                    case 9 -> image = right9;
                }
            }

        }
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }
}
