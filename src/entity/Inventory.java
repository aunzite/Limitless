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

    // Add hovered slot tracking
    private int hoveredRow = -1;
    private int hoveredCol = -1;

    public Inventory(GamePanel gp) {
        this.gp = gp;
    }

    public void toggle() { isOpen = !isOpen; closeMenus(); }
    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { this.isOpen = open; closeMenus(); }

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
                    int gridWidth = COLS * SLOT_WIDTH + (COLS - 1) * SLOT_GAP;
                    int gridHeight = ROWS * SLOT_HEIGHT + (ROWS - 1) * SLOT_GAP;
                    int gridStartX = (gp.screenWidth - gridWidth) / 2;
                    int gridStartY = (gp.screenHeight - gridHeight) / 2 + OFFSET_TOP - 60;
                    int slotX = gridStartX + col * (SLOT_WIDTH + SLOT_GAP);
                    int slotY = gridStartY + row * (SLOT_HEIGHT + SLOT_GAP);
                    dragOffsetX = mouseX - slotX;
                    dragOffsetY = mouseY - slotY;
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
        // Track hovered slot for highlight
        int[] slot = getSlotAt(mouseX, mouseY);
        if (slot != null) {
            hoveredRow = slot[0];
            hoveredCol = slot[1];
        } else {
            hoveredRow = -1;
            hoveredCol = -1;
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
                int x = getSlotX(j)-50;
                int y = getSlotY(i)-50;
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
                // Drop only one item
                if (item.getName().toLowerCase().contains("apple")) {
                    boolean stacked = false;
                    for (int i = 0; i < gp.obj.length; i++) {
                        if (gp.obj[i] instanceof object.OBJ_Apple) {
                            object.OBJ_Apple dropped = (object.OBJ_Apple)gp.obj[i];
                            int hitbox = gp.tileSize; // 1 tile radius
                            int dx = dropped.worldX - gp.player.worldX;
                            int dy = dropped.worldY - gp.player.worldY;
                            if (Math.abs(dx) < hitbox && Math.abs(dy) < hitbox) {
                                dropped.quantity += 1;
                                stacked = true;
                                break;
                            }
                        }
                    }
                    if (!stacked) {
                        object.OBJ_Apple droppedApple = new object.OBJ_Apple(1);
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
                // Decrease quantity or remove item
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    items[contextMenuRow][contextMenuCol] = null;
                }
                closeMenus();
                break;
            case 1: // Use/Equip/Unequip
                if (item.getName().toLowerCase().contains("apple")) {
                    // Apple restores 20 health and 15 stamina
                    gp.player.hp = Math.min(100, gp.player.hp + 20);
                    int newStamina = Math.min(1000, gp.player.stamina + 150); // Scale up to 0-1000
                    gp.player.stamina = newStamina;
                    gp.hud.setStamina(newStamina); // Keep HUD and player in sync
                    // Decrease quantity or remove item
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                    } else {
                        items[contextMenuRow][contextMenuCol] = null;
                    }
                } else if (item.getName().toLowerCase().contains("food")) {
                    // Example: generic food restores 10 health and stamina
                    gp.player.hp = Math.min(100, gp.player.hp + 10);
                    int newStamina = Math.min(1000, gp.player.stamina + 100); // Scale up to 0-1000
                    gp.player.stamina = newStamina;
                    gp.hud.setStamina(newStamina); // Keep HUD and player in sync
                    // Decrease quantity or remove item
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                    } else {
                        items[contextMenuRow][contextMenuCol] = null;
                    }
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
        // Draw semi-transparent background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Calculate grid dimensions (remove OFFSET_LEFT for true centering)
        int gridWidth = COLS * SLOT_WIDTH + (COLS - 1) * SLOT_GAP;
        int gridHeight = ROWS * SLOT_HEIGHT + (ROWS - 1) * SLOT_GAP;
        int gridStartX = (gp.screenWidth - gridWidth) / 2;
        int gridStartY = (gp.screenHeight - gridHeight) / 2 + OFFSET_TOP - 60;

        // Draw Inventory background panel with checkerboard
        int panelPaddingX = 40;
        int panelPaddingY = 100;
        int panelX = gridStartX - panelPaddingX;
        int panelY = gridStartY - panelPaddingY;
        int panelW = gridWidth + panelPaddingX * 2;
        int panelH = gridHeight + panelPaddingY * 2 - 20;
        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(panelX + 8, panelY + 8, panelW, panelH, 16, 16);
        // Checkerboard background
        int checkerSize = 16;
        for (int y = 0; y < panelH; y += checkerSize) {
            for (int x = 0; x < panelW; x += checkerSize) {
                if (((x / checkerSize) + (y / checkerSize)) % 2 == 0) {
                    g2.setColor(new Color(50, 40, 50, 60));
                } else {
                    g2.setColor(new Color(30, 20, 30, 60));
                }
                g2.fillRect(panelX + x, panelY + y, checkerSize, checkerSize);
            }
        }
        // Panel base
        g2.setColor(new Color(40, 30, 30, 220));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 16, 16);
        // Pixel-style double border
        g2.setColor(new Color(200, 200, 255, 180));
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 16, 16);
        g2.setColor(new Color(120, 120, 180, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(panelX + 6, panelY + 6, panelW - 12, panelH - 12, 10, 10);

        // Draw Inventory title with pixel shadow
        String title = "Inventory";
        Font titleFont = new Font("Comic Sans MS", Font.BOLD, 44);
        g2.setFont(titleFont);
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (gp.screenWidth - fm.stringWidth(title)) / 2;
        int titleY = panelY + 75;
        g2.setColor(new Color(40, 30, 60, 180));
        g2.drawString(title, titleX + 2, titleY + 2); // shadow
        g2.setColor(Color.WHITE);
        g2.drawString(title, titleX, titleY);

        // Draw right-click hint at the bottom, fading in and out
        long time = System.currentTimeMillis();
        int alpha = (int)(120 + 100 * Math.abs(Math.sin(time / 600.0)));
        g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 18));
        String hint = "Right-click items for more options";
        int hintWidth = g2.getFontMetrics().stringWidth(hint);
        int hintX = (gp.screenWidth - hintWidth) / 2;
        int hintY = panelY + panelH - 30;
        g2.setColor(new Color(255, 255, 255, alpha));
        g2.drawString(hint, hintX, hintY);

        // Draw grid with pixel-art slots
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = gridStartX + j * (SLOT_WIDTH + SLOT_GAP);
                int y = gridStartY + i * (SLOT_HEIGHT + SLOT_GAP);
                // Slot shadow
                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillRoundRect(x + 4, y + 6, SLOT_WIDTH, SLOT_HEIGHT, 10, 10);
                // Slot base (more saturated brown)
                g2.setColor(new Color(120, 80, 50));
                g2.fillRoundRect(x, y, SLOT_WIDTH, SLOT_HEIGHT, 10, 10);
                // Slot highlight (pixel-art style)
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(x + 2, y + 2, SLOT_WIDTH - 4, SLOT_HEIGHT - 4, 6, 6);
                // Slot double border (pixel frame effect)
                g2.setColor(new Color(200, 200, 255, 120));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x, y, SLOT_WIDTH, SLOT_HEIGHT, 10, 10);
                g2.setColor(new Color(80, 80, 120, 120));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(x + 3, y + 3, SLOT_WIDTH - 6, SLOT_HEIGHT - 6, 4, 4);
            }
        }

        // Draw items
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Item item = items[i][j];
                if (item != null) {
                    int x = gridStartX + j * (SLOT_WIDTH + SLOT_GAP);
                    int y = gridStartY + i * (SLOT_HEIGHT + SLOT_GAP);
                    if (item.isDragging()) {
                        continue; // Don't draw here, draw dragged item on top
                    }
                    // Draw item image
                    g2.drawImage(item.getImage(), x + 4, y + 4, SLOT_WIDTH - 8, SLOT_HEIGHT - 8, null);
                    // Draw quantity if more than 1
                    if (item.getQuantity() > 1) {
                        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
                        g2.setColor(Color.WHITE);
                        String quantity = String.valueOf(item.getQuantity());
                        g2.drawString(quantity, x + SLOT_WIDTH - 20, y + SLOT_HEIGHT - 5);
                    }
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
            g2.drawImage(draggedItem.getImage(), drawX + 4, drawY + 4, SLOT_WIDTH - 8, SLOT_HEIGHT - 8, null);
            if (draggedItem.getQuantity() > 1) {
                g2.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
                g2.setColor(Color.WHITE);
                String quantity = String.valueOf(draggedItem.getQuantity());
                g2.drawString(quantity, drawX + SLOT_WIDTH - 20, drawY + SLOT_HEIGHT - 5);
            }
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

    public static void drawDetailsPopupBox(Graphics2D g2, int x, int y, int w, int h, String name, String[] lines, int quantity) {
        g2.setColor(new Color(30, 30, 30, 240));
        g2.fillRoundRect(x, y, w, h, 16, 16);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, w, h, 16, 16);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        String title = name + (quantity > 1 ? " x" + quantity : "");
        g2.drawString(title, x + 16, y + 28);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
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
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        String title = item.getName() + (item.getQuantity() > 1 ? " x" + item.getQuantity() : "");
        g2.drawString(title, x + 16, y + 32);
        // Buttons
        String[] btns = {"Drop", item.getName().equalsIgnoreCase("Solthorn") && gp.player.weapon != null && gp.player.weapon.getName().equalsIgnoreCase("Solthorn") ? "Unequip" : "Use", "Details"};
        for (int i = 0; i < 3; i++) {
            int btnY = y + 40 + i * (BUTTON_HEIGHT + BUTTON_MARGIN + 10); // Add 10px extra gap for more vertical space
            g2.setColor(i == hoveredButton ? new Color(100, 100, 255) : new Color(60, 60, 60));
            g2.fillRoundRect(x + 10, btnY, MENU_WIDTH - 20, BUTTON_HEIGHT, 8, 8);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(x + 10, btnY, MENU_WIDTH - 20, BUTTON_HEIGHT, 8, 8);
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
            g2.drawString(btns[i], x + 30, btnY + 22);
        }
    }
}