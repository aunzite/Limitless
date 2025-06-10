package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import main.GamePanel;

public class Inventory {
    // Layout constants
    private static final int ROWS = 4;
    private static final int COLS = 8;
    private static final int SLOT_WIDTH = 120;
    private static final int SLOT_HEIGHT = 110;
    private static final int SLOT_GAP = 27;
    private static final int OFFSET_LEFT = 45;
    private static final int OFFSET_TOP = 50;
    private static final double ITEM_SCALE = 0.8;

    public Item[][] items = new Item[ROWS][COLS];
    private int draggedItemOriginalRow, draggedItemOriginalCol;
    private Item draggedItem = null;
    private int dragOffsetX, dragOffsetY;
    private boolean isOpen = false;
    private GamePanel gp;

    // Context menu state
    private boolean contextMenuOpen = false;
    private int contextMenuRow = -1, contextMenuCol = -1;
    private int contextMenuX = 0, contextMenuY = 0;
    private static final int MENU_WIDTH = 200;
    private static final int MENU_HEIGHT = 140;
    private static final int BUTTON_HEIGHT = 32;
    private static final int BUTTON_MARGIN = 8;
    private int hoveredButton = -1; // 0: Drop, 1: Use/Equip, 2: Details
    private boolean detailsPopupOpen = false;

    public Inventory(GamePanel gp) {
        this.gp = gp;
    }

    public void toggle() { isOpen = !isOpen; closeMenus(); }
    public boolean isOpen() { return isOpen; }

    public void addItem(Item item) {
        // Stack apples if possible
        if (item.getName().equalsIgnoreCase("Apple")) {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    Item invItem = items[i][j];
                    if (invItem != null && invItem.getName().equalsIgnoreCase("Apple")) {
                        invItem.setQuantity(invItem.getQuantity() + item.getQuantity());
                        return;
                    }
                }
            }
        }
        // Otherwise, add to first empty slot
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (items[i][j] == null) {
                    items[i][j] = item;
                    return;
                }
            }
        }
    }

    public void handleMousePress(int mouseX, int mouseY, boolean isRightClick) {
        if (!isOpen) return;
        if (contextMenuOpen) {
            int btn = getMenuButtonAt(mouseX, mouseY);
            if (btn != -1) {
                handleMenuAction(btn);
                return;
            }
            closeMenus();
            return;
        }
        int[] slot = getSlotAt(mouseX, mouseY);
        if (slot != null) {
            int row = slot[0], col = slot[1];
            Item item = items[row][col];
            if (item != null) {
                if (isRightClick) {
                    // Open context menu on right-click
                    contextMenuOpen = true;
                    contextMenuRow = row;
                    contextMenuCol = col;
                    contextMenuX = mouseX;
                    contextMenuY = mouseY;
                    detailsPopupOpen = false;
                    return;
                } else {
                    // Start dragging on left-click
                    draggedItem = item;
                    draggedItemOriginalRow = row;
                    draggedItemOriginalCol = col;
                    dragOffsetX = mouseX - getSlotX(col);
                    dragOffsetY = mouseY - getSlotY(row);
                    items[row][col] = null;
                    return;
                }
            }
        }
        closeMenus();
    }

    public void handleMouseDrag(int mouseX, int mouseY) {
        // Only update if dragging
        // (No-op for context menu)
    }

    public void handleMouseRelease(int mouseX, int mouseY) {
        if (draggedItem != null) {
            int[] slot = getSlotAt(mouseX, mouseY);
            if (slot != null) {
                int row = slot[0], col = slot[1];
                if (items[row][col] == null) {
                    items[row][col] = draggedItem;
                } else {
                    // Swap items
                    Item temp = items[row][col];
                    items[row][col] = draggedItem;
                    items[draggedItemOriginalRow][draggedItemOriginalCol] = temp;
                }
            } else {
                // Return to original slot
                items[draggedItemOriginalRow][draggedItemOriginalCol] = draggedItem;
            }
            draggedItem = null;
        }
    }

    public void handleMouseMove(int mouseX, int mouseY) {
        if (contextMenuOpen) {
            hoveredButton = getMenuButtonAt(mouseX, mouseY);
        }
    }

    private void closeMenus() {
        contextMenuOpen = false;
        detailsPopupOpen = false;
        hoveredButton = -1;
    }

    private int getSlotX(int col) {
        int gridWidth = COLS * SLOT_WIDTH + (COLS - 1) * SLOT_GAP;
        int startX = (gp.screenWidth - gridWidth) / 2 + OFFSET_LEFT;
        return startX + col * (SLOT_WIDTH + SLOT_GAP);
    }
    private int getSlotY(int row) {
        int gridHeight = ROWS * SLOT_HEIGHT + (ROWS - 1) * SLOT_GAP;
        int startY = (gp.screenHeight - gridHeight) / 2 + OFFSET_TOP;
        return startY + row * (SLOT_HEIGHT + SLOT_GAP);
    }
    private int[] getSlotAt(int mouseX, int mouseY) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = getSlotX(j);
                int y = getSlotY(i);
                if (mouseX >= x && mouseX < x + SLOT_WIDTH && mouseY >= y && mouseY < y + SLOT_HEIGHT) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
    private int getMenuButtonAt(int mouseX, int mouseY) {
        if (!contextMenuOpen) return -1;
        int x = contextMenuX;
        int y = contextMenuY;
        for (int i = 0; i < 3; i++) {
            int btnY = y + 40 + i * (BUTTON_HEIGHT + BUTTON_MARGIN + 10);
            if (mouseX >= x + 10 && mouseX <= x + MENU_WIDTH - 10 && mouseY >= btnY && mouseY <= btnY + BUTTON_HEIGHT) {
                return i;
            }
        }
        return -1;
    }

    private void handleMenuAction(int btn) {
        Item item = items[contextMenuRow][contextMenuCol];
        if (item == null) return;
        switch (btn) {
            case 0: // Drop
                // Create a dropped item in the world, stack if near
                if (item.getName().toLowerCase().contains("apple")) {
                    boolean stacked = false;
                    for (int i = 0; i < gp.obj.length; i++) {
                        if (gp.obj[i] instanceof object.OBJ_Apple) {
                            object.OBJ_Apple dropped = (object.OBJ_Apple)gp.obj[i];
                            int hitbox = gp.tileSize; // 1 tile radius
                            int dx = dropped.worldX - gp.player.worldX;
                            int dy = dropped.worldY - gp.player.worldY;
                            if (Math.abs(dx) < hitbox && Math.abs(dy) < hitbox) {
                                dropped.quantity += item.getQuantity();
                                stacked = true;
                                break;
                            }
                        }
                    }
                    if (!stacked) {
                        object.OBJ_Apple droppedApple = new object.OBJ_Apple(item.getQuantity());
                        droppedApple.worldX = gp.player.worldX;
                        droppedApple.worldY = gp.player.worldY;
                        for (int i = 0; i < gp.obj.length; i++) {
                            if (gp.obj[i] == null) {
                                gp.obj[i] = droppedApple;
                                break;
                            }
                        }
                    }
                } else if (item.getName().equalsIgnoreCase("Solthorn")) {
                    object.OBJ_Solthorn droppedSolthorn = new object.OBJ_Solthorn();
                    droppedSolthorn.worldX = gp.player.worldX;
                    droppedSolthorn.worldY = gp.player.worldY;
                    for (int i = 0; i < gp.obj.length; i++) {
                        if (gp.obj[i] == null) {
                            gp.obj[i] = droppedSolthorn;
                            break;
                        }
                    }
                    // If Solthorn was equipped, unequip and revert textures
                    if (gp.player.weapon != null && gp.player.weapon.getName().equalsIgnoreCase("Solthorn")) {
                        gp.player.weapon = null;
                        gp.player.setSwordTextures(false);
                    }
                }
                items[contextMenuRow][contextMenuCol] = null;
                closeMenus();
                break;
            case 1: // Use/Equip/Unequip
                if (item.getName().toLowerCase().contains("apple")) {
                    // Apple restores 20 health and 15 stamina
                    gp.player.hp = Math.min(100, gp.player.hp + 20);
                    int newStamina = Math.min(1000, gp.player.stamina + 150); // Scale up to 0-1000
                    gp.player.stamina = newStamina;
                    gp.hud.setStamina(newStamina); // Keep HUD and player in sync
                    items[contextMenuRow][contextMenuCol] = null;
                } else if (item.getName().toLowerCase().contains("food")) {
                    // Example: generic food restores 10 health and stamina
                    gp.player.hp = Math.min(100, gp.player.hp + 10);
                    int newStamina = Math.min(1000, gp.player.stamina + 100); // Scale up to 0-1000
                    gp.player.stamina = newStamina;
                    gp.hud.setStamina(newStamina); // Keep HUD and player in sync
                    items[contextMenuRow][contextMenuCol] = null;
                } else if (item.getName().equalsIgnoreCase("Solthorn")) {
                    if (gp.player.weapon != null && gp.player.weapon.getName().equalsIgnoreCase("Solthorn")) {
                        // Unequip
                        gp.player.weapon = null;
                        gp.player.setSwordTextures(false);
                    } else {
                        // Equip
                        gp.player.weapon = new Weapon(item.getName(), 25, 1.0, "sword");
                        gp.player.setSwordTextures(true);
                    }
                } else if (item.getName().toLowerCase().contains("sword") || item.getName().toLowerCase().contains("weapon")) {
                    gp.player.weapon = new Weapon(item.getName(), 10, 1.0, "sword");
                    gp.player.setSwordTextures(false);
                }
                closeMenus();
                break;
            case 2: // Details
                detailsPopupOpen = true;
                break;
        }
    }

    public void draw(Graphics2D g2) {
        if (!isOpen) return;
        // Draw overlay
        g2.setColor(new Color(0, 0, 0, 128));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Draw right-click hint
        g2.setFont(new Font("Arial", Font.ITALIC, 16));
        String hint = "Right-click items for more options";
        int hintWidth = g2.getFontMetrics().stringWidth(hint);
        int hintX = (gp.screenWidth - hintWidth) / 2;
        int hintY = 30;
        g2.setColor(new Color(255, 255, 255, 180));
        g2.drawString(hint, hintX, hintY);
        
        // Draw grid
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = getSlotX(j);
                int y = getSlotY(i);
                g2.setColor(new Color(60, 40, 30));
                g2.fillRect(x, y, SLOT_WIDTH, SLOT_HEIGHT);
                g2.setColor(Color.WHITE);
                g2.drawRect(x, y, SLOT_WIDTH, SLOT_HEIGHT);
            }
        }
        // Draw items
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Item item = items[i][j];
                if (item != null && item != draggedItem) {
                    drawItem(g2, item, getSlotX(j), getSlotY(i));
                }
            }
        }
        // Draw dragged item on top
        if (draggedItem != null) {
            PointerInfo pi = MouseInfo.getPointerInfo();
            Point mp = pi.getLocation();
            SwingUtilities.convertPointFromScreen(mp, gp);
            int drawX = mp.x - dragOffsetX;
            int drawY = mp.y - dragOffsetY;
            drawItem(g2, draggedItem, drawX, drawY);
        }
        // Draw context menu if open
        if (contextMenuOpen) {
            drawContextMenu(g2);
        }
        // Draw details popup if open
        if (detailsPopupOpen) {
            drawDetailsPopup(g2);
        }
    }

    private void drawItem(Graphics2D g2, Item item, int x, int y) {
        BufferedImage img = item.getImage();
        int drawW = (int)(SLOT_WIDTH * ITEM_SCALE);
        int drawH = (int)(SLOT_HEIGHT * ITEM_SCALE);
        int drawX = x + (SLOT_WIDTH - drawW) / 2;
        int drawY = y + (SLOT_HEIGHT - drawH) / 2;
        g2.drawImage(img, drawX, drawY, drawW, drawH, null);
    }

    private void drawContextMenu(Graphics2D g2) {
        int x = contextMenuX;
        int y = contextMenuY;
        Item item = items[contextMenuRow][contextMenuCol];
        if (item == null) return;
        // Make the menu taller
        int menuHeight = MENU_HEIGHT + 40; // Increase height by 40px
        // Menu background
        g2.setColor(new Color(30, 30, 30, 240));
        g2.fillRoundRect(x, y, MENU_WIDTH, menuHeight, 16, 16);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, MENU_WIDTH, menuHeight, 16, 16);
        // Item name and quantity
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        String title = item.getName() + (item.getQuantity() > 1 ? " x" + item.getQuantity() : "");
        g2.drawString(title, x + 16, y + 32);
        // Buttons
        String[] btns;
        if (item.getName().equalsIgnoreCase("Solthorn")) {
            btns = new String[]{"Drop", "Use", "Details"};
        } else {
            btns = new String[]{"Drop", "Use", "Details"};
        }
        for (int i = 0; i < 3; i++) {
            int btnY = y + 40 + i * (BUTTON_HEIGHT + BUTTON_MARGIN + 10); // Add 10px extra gap for more vertical space
            g2.setColor(i == hoveredButton ? new Color(100, 100, 255) : new Color(60, 60, 60));
            g2.fillRoundRect(x + 10, btnY, MENU_WIDTH - 20, BUTTON_HEIGHT, 8, 8);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(x + 10, btnY, MENU_WIDTH - 20, BUTTON_HEIGHT, 8, 8);
            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.drawString(btns[i], x + 30, btnY + 22);
        }
    }

    public static void drawDetailsPopupBox(Graphics2D g2, int x, int y, int w, int h, String name, String[] lines, int quantity) {
        g2.setColor(new Color(30, 30, 30, 240));
        g2.fillRoundRect(x, y, w, h, 16, 16);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, w, h, 16, 16);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        String title = name + (quantity > 1 ? " x" + quantity : "");
        g2.drawString(title, x + 16, y + 28);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        int lineY = y + 52;
        for (String line : lines) {
            g2.drawString(line, x + 16, lineY);
            lineY += 20;
        }
    }

    private void drawDetailsPopup(Graphics2D g2) {
        int x = contextMenuX + MENU_WIDTH + 10;
        int y = contextMenuY;
        Item item = items[contextMenuRow][contextMenuCol];
        if (item == null) return;
        int w = 340, h = 160; // Increased width and height
        String[] descLines;
        if (item.getName().toLowerCase().contains("apple")) {
            descLines = new String[]{"A fresh, juicy apple that restores your vitality.", "Effect: Restores 20 health and 15 stamina."};
        } else if (item.getName().equalsIgnoreCase("Solthorn")) {
            descLines = new String[]{
                "A legendary blade passed down through",
                "Elaria's bloodline, forged around a gem said",
                "to hold unimaginable power."
            };
        } else if (item.getName().toLowerCase().contains("sword")) {
            descLines = new String[]{"Type: Weapon", "(More info here...)"};
        } else {
            descLines = new String[]{"Type: Other", "(More info here...)"};
        }
        drawDetailsPopupBox(g2, x, y, w, h, item.getName(), descLines, item.getQuantity());
    }
}