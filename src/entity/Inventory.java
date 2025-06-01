package entity;

public class Inventory {
    private Object[][] items;
    private int rows;
    private int cols;
    private int currentItems;
    private int maxCapacity;

    // Constructor to initialize the inventory with specified dimensions
    public Inventory(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.items = new Object[rows][cols];
        this.maxCapacity = rows * cols;
        this.currentItems = 0;
    }

    // Add an item to the first available slot
    public boolean addItem(Object item) {
        if (currentItems >= maxCapacity) {
            return false;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (items[i][j] == null) {
                    items[i][j] = item;
                    currentItems++;
                    return true;
                }
            }
        }
        return false;
    }

    // Add an item to a specific position
    public boolean addItemAt(Object item, int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        
        if (items[row][col] != null) {
            return false;
        }

        items[row][col] = item;
        currentItems++;
        return true;
    }

    // Remove an item from a specific position
    public Object removeItem(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;
        }

        Object item = items[row][col];
        if (item != null) {
            items[row][col] = null;
            currentItems--;
        }
        return item;
    }

    // Get item at specific position
    public Object getItem(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;
        }
        return items[row][col];
    }

    // Check if inventory is full
    public boolean isFull() {
        return currentItems >= maxCapacity;
    }

    // Get number of items currently in inventory
    public int getCurrentItems() {
        return currentItems;
    }

    // Get maximum capacity
    public int getMaxCapacity() {
        return maxCapacity;
    }

    // Display inventory contents
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (items[i][j] == null) {
                    sb.append("[ empty ]");
                } else {
                    sb.append("[ ").append(items[i][j].toString()).append(" ]");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}