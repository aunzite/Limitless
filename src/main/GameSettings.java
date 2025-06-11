/////////////////////////////////////////////////////////////////////////////
// Limitless
// GameSettings.java
// 
// Description: Manages game settings including:
// - Auto-save settings
// - Key bindings
// - Game preferences
/////////////////////////////////////////////////////////////////////////////

package main;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// Manages game settings and preferences
public class GameSettings {
    private static final String SETTINGS_FILE = "settings.txt";
    private static GameSettings instance;
    
    // Settings
    private boolean autoSaveEnabled = true;
    private Map<String, Integer> keybinds;
    
    private GameSettings() {
        keybinds = new HashMap<>();
        setDefaultKeybinds();
        loadSettings();
    }
    
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    private void setDefaultKeybinds() {
        keybinds.put("up", java.awt.event.KeyEvent.VK_W);
        keybinds.put("down", java.awt.event.KeyEvent.VK_S);
        keybinds.put("left", java.awt.event.KeyEvent.VK_A);
        keybinds.put("right", java.awt.event.KeyEvent.VK_D);
        keybinds.put("sprint", java.awt.event.KeyEvent.VK_SHIFT);
        keybinds.put("interact", java.awt.event.KeyEvent.VK_E);
        keybinds.put("inventory", java.awt.event.KeyEvent.VK_I);
    }
    
    public void setAutoSaveEnabled(boolean enabled) {
        this.autoSaveEnabled = enabled;
        saveSettings();
    }
    
    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }
    
    public void setKeybind(String action, int keyCode) {
        keybinds.put(action, keyCode);
        saveSettings();
    }
    
    public int getKeybind(String action) {
        return keybinds.getOrDefault(action, 0);
    }
    
    public Map<String, Integer> getAllKeybinds() {
        return new HashMap<>(keybinds);
    }
    
    public void saveSettings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SETTINGS_FILE))) {
            writer.println("autosave=" + autoSaveEnabled);
            for (Map.Entry<String, Integer> entry : keybinds.entrySet()) {
                writer.println("keybind:" + entry.getKey() + "=" + entry.getValue());
            }
        } catch (IOException e) {
            // Handle error silently
        }
    }
    
    public void loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) {
            saveSettings();
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length != 2) continue;
                
                String key = parts[0];
                String value = parts[1];
                
                try {
                    if (key.equals("autosave")) {
                        autoSaveEnabled = Boolean.parseBoolean(value);
                    } else if (key.startsWith("keybind:")) {
                        String action = key.substring(8);
                        keybinds.put(action, Integer.parseInt(value));
                    }
                } catch (NumberFormatException e) {
                    // Handle error silently
                }
            }
        } catch (IOException e) {
            // Handle error silently
        }
    }
} 