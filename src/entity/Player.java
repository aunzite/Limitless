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

        x = 100;
        y = 100;
        speed = 4;
        direction = "down";
    }
    public void getPlayerImage(){

        try {
            
            // Create a res folder in your project root and put images there
            String resourcePath = "res";
            
            up1 = ImageIO.read(new File(resourcePath + "/player/back1.png"));
            up2 = ImageIO.read(new File(resourcePath + "/player/back2.png"));
            up3 = ImageIO.read(new File(resourcePath + "/player/back3.png"));
            down1 = ImageIO.read(new File(resourcePath + "/player/front1.png"));
            down2 = ImageIO.read(new File(resourcePath + "/player/front2.png"));
            left1 = ImageIO.read(new File(resourcePath + "/player/left1.png"));
            left2 = ImageIO.read(new File(resourcePath + "/player/left2.png"));
            left3 = ImageIO.read(new File(resourcePath + "/player/left3.png"));
            right1 = ImageIO.read(new File(resourcePath + "/player/right1.png"));
            right2 = ImageIO.read(new File(resourcePath + "/player/right2.png"));
            right3 = ImageIO.read(new File(resourcePath + "/player/right3.png"));
            
        } catch (IOException e) {
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
    
            spriteCounter++;
            if(spriteCounter > 12){
                if (spriteNum == 1){
                    spriteNum = 2;
                } else if (spriteNum == 2){
                    spriteNum = 3;
                }else if (spriteNum == 3){
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }
    }
    
    public void draw(Graphics2D g2){
        

        BufferedImage image = null;
        switch(direction){
            case "up":
                if (spriteNum == 1){
                    image = up1;
                } else if (spriteNum == 2){
                    image = up2;
                } else if (spriteNum == 3){
                    image = up3;
                }
                break;
            case "down":
                if (spriteNum == 1){
                    image = down1;
                } else if (spriteNum == 2){
                    image = down2;
                } else if (spriteNum == 3){
                    image = down1;
                }
                break;
            case "left":
                if (spriteNum == 1){
                    image = left1;
                } else if (spriteNum == 2){
                    image = left2;
                } else if (spriteNum == 3){
                    image = left3;
                }
                break;
            case "right":
                if (spriteNum == 1){
                    image = right1;
                } else if (spriteNum == 2){
                    image = right2;
                } else if (spriteNum == 3){
                    image = right3;
                }
                break;
        }
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }
}
