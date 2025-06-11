package entity;

//imports
import java.awt.*;
import main.*;

public class EnvironmentInteraction {

    //variables
    //game panel and key handler
    GamePanel gp;
    KeyHandler keyH;

    //states
    private boolean inRange;
    private boolean inDialogue;
    private boolean paragraphFullyShown;

    //dialogue
    private int currentParagraph;
    private String[] paragraphs;
    private StringBuilder visibleText;
    private int scrollIndex;
    private long lastScrollTime;
    private static final int SCROLL_DELAY = 30;

    //interaction
    private String interactionName;
    private int worldX;
    private int worldY;
    private int interactionRadius;
    private long lastInteractionTime;
    private static final long INTERACTION_COOLDOWN = 1000;

    //constructor
    public EnvironmentInteraction(GamePanel gp, KeyHandler keyH, String name, int x, int y, int radius, String[] dialogue) {

        //set game panel and key handler
        this.gp = gp;
        this.keyH = keyH;

        //set interaction name
        this.interactionName = name;

        //set position
        this.worldX = x;
        this.worldY = y;

        //set radius
        this.interactionRadius = radius;

        //set dialogue
        this.paragraphs = dialogue;

        //create visible text
        this.visibleText = new StringBuilder();
    }

    //update
    public void update() {
        
        //if in dialogue
        if (inDialogue) {

            //if escape is pressed
            if (keyH.escapePressed) {

                //end dialogue
                inDialogue = false;
                currentParagraph = 0;
                gp.gameState = GamePanel.PLAY_STATE;
                keyH.escapePressed = false;
                lastInteractionTime = System.currentTimeMillis();
                return;
            }

            //if e is pressed
            if (keyH.ePressed) {

                //handle dialogue
                handleDialogue();
                keyH.ePressed = false;
            }

            //if paragraph is not fully shown
            if (!paragraphFullyShown && currentParagraph < paragraphs.length) {

                //get current time
                long now = System.currentTimeMillis();

                //if time since last scroll is greater than scroll delay
                if (now - lastScrollTime > SCROLL_DELAY) {
                    //if scroll index is less than paragraph length
                    if (scrollIndex < paragraphs[currentParagraph].length()) {
                        //add character to visible text
                        visibleText.append(paragraphs[currentParagraph].charAt(scrollIndex));
                        //increment scroll index
                        scrollIndex++;
                        //set last scroll time
                        lastScrollTime = now;
                    } else {
                        //set paragraph fully shown to true
                        paragraphFullyShown = true;
                    }
                }
            }
            return;
        }

        //get distance to player
        int dx = Math.abs(worldX - gp.player.worldX);
        int dy = Math.abs(worldY - gp.player.worldY);
        int distance = (int) Math.sqrt(dx * dx + dy * dy);

        //set in range
        inRange = distance < interactionRadius;

        //if in range
        if (inRange) {
            //get current time
            long currentTime = System.currentTimeMillis();

            //if e is pressed and cooldown is over
            if (keyH.ePressed && currentTime - lastInteractionTime >= INTERACTION_COOLDOWN) {
                //handle dialogue
                handleDialogue();
                keyH.ePressed = false;
            }
        }
    }

    //handle dialogue
    private void handleDialogue() {
        //if not in dialogue
        if (!inDialogue) {
            //start dialogue
            inDialogue = true;
            currentParagraph = 0;
            visibleText.setLength(0);
            scrollIndex = 0;
            paragraphFullyShown = false;
            lastScrollTime = System.currentTimeMillis();
            gp.gameState = GamePanel.DIALOGUE_STATE;
        } else {
            //if paragraph is not fully shown
            if (!paragraphFullyShown) {
                //set visible text to full paragraph
                visibleText = new StringBuilder(paragraphs[currentParagraph]);
                scrollIndex = paragraphs[currentParagraph].length();
                paragraphFullyShown = true;
            } else {
                //increment current paragraph
                currentParagraph++;

                //if current paragraph is greater than or equal to paragraphs length
                if (currentParagraph >= paragraphs.length) {
                    //end dialogue
                    inDialogue = false;
                    currentParagraph = 0;

                    //if interaction name is Ancient Shrine
                    if (interactionName.equals("Ancient Shrine")) {
                        //get platform position
                        int platformX = (gp.screenWidth - (16 * gp.tileSize)) / 2;
                        int platformY = (gp.screenHeight - (8 * gp.tileSize)) / 2;

                        //set player position
                        gp.player.worldX = platformX + gp.tileSize;
                        gp.player.worldY = platformY + (4 * gp.tileSize);
                        gp.player.direction = "right";
                        gp.player.inventory.setOpen(false);

                        //if player has sword
                        if (gp.player.weapon != null && gp.player.weapon.getName().toLowerCase().contains("sword")) {
                            gp.player.setSwordTextures(true);
                        } else {
                            gp.player.setSwordTextures(false);
                        }

                        //stop music
                        main.AudioManager.getInstance().stopMusic();

                        //get boss position
                        int bossX = platformX + (int)(16 * gp.tileSize * 0.75);
                        int bossY = platformY + (8 * gp.tileSize) / 2 - 64;

                        //create boss
                        gp.bossNoxar = new entity.BossNoxar(bossX, bossY);
                        gp.bossNoxar.direction = "left";

                        //start cutscene
                        gp.noxarCutsceneIndex = 0;
                        gp.inNoxarCutscene = true;
                        gp.gameState = main.GamePanel.NOXAR_CUTSCENE_STATE;
                    } else {
                        //set game state to play
                        gp.gameState = GamePanel.PLAY_STATE;
                    }

                    //set last interaction time
                    lastInteractionTime = System.currentTimeMillis();
                } else {
                    //start next paragraph
                    visibleText.setLength(0);
                    scrollIndex = 0;
                    paragraphFullyShown = false;
                    lastScrollTime = System.currentTimeMillis();
                }
            }
        }
    }

    //draw
    public void draw(Graphics2D g2) {
        //get screen position
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        //if in range
        if (inRange) {
            //if in dialogue
            if (inDialogue) {
                //draw dialogue box
                drawDialogueBox(g2);
            } else {
                //draw interaction message
                drawInteractionMessage(g2, screenX, screenY);
            }
        }
    }

    //draw interaction message
    private void drawInteractionMessage(Graphics2D g2, int screenX, int screenY) {
        //set message
        String message = "Press E to interact";

        //set font
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 16));

        //get message width
        int messageWidth = g2.getFontMetrics().stringWidth(message);

        //set background color
        g2.setColor(new Color(0, 0, 0, 180));

        //draw background
        g2.fillRoundRect(
            screenX + (gp.tileSize - messageWidth) / 2 - 5,
            screenY - 30,
            messageWidth + 10,
            25,
            10,
            10
        );

        //set text color
        g2.setColor(Color.WHITE);

        //draw message
        g2.drawString(
            message,
            screenX + (gp.tileSize - messageWidth) / 2,
            screenY - 10
        );
    }

    //draw dialogue box
    private void drawDialogueBox(Graphics2D g2) {
        //get box position
        int boxX = 60;
        int boxY = gp.screenHeight - 200;
        int boxWidth = gp.screenWidth - 120;
        int boxHeight = 150;

        //set background color
        g2.setColor(new Color(0, 0, 0, 200));

        //draw background
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        //set border color
        g2.setColor(Color.WHITE);

        //draw border
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        //set font
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));

        //draw text
        drawStringMultiLine(g2, visibleText.toString(), boxX + 20, boxY + 30, boxWidth - 40);
    }

    //draw string multi line
    private void drawStringMultiLine(Graphics2D g2, String text, int x, int y, int maxWidth) {
        //get font metrics
        FontMetrics metrics = g2.getFontMetrics();

        //get line height
        int lineHeight = metrics.getHeight();

        //get words
        String[] words = text.split(" ");

        //set current line
        String currentLine = "";

        //loop through words
        for (String word : words) {
            //get test line
            String testLine = currentLine + word + " ";

            //get test line width
            int testLineWidth = metrics.stringWidth(testLine);

            //if test line width is greater than max width
            if (testLineWidth > maxWidth) {
                //draw current line
                g2.drawString(currentLine, x, y);

                //set current line to word
                currentLine = word + " ";

                //increment y
                y += lineHeight;
            } else {
                //set current line to test line
                currentLine = testLine;
            }
        }

        //draw last line
        g2.drawString(currentLine, x, y);
    }
} 