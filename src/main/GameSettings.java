package main;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GameSettings {
    private static final String SETTINGS_FILE = "settings.txt";
    private static GameSettings instance;
    
    // Settings
    private float brightness = 1.0f;
    private float contrast = 1.0f;
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
    
    public void setBrightness(float brightness) {
        this.brightness = Math.max(0.0f, Math.min(2.0f, brightness));
        saveSettings();
    }
    
    public void setContrast(float contrast) {
        this.contrast = Math.max(0.0f, Math.min(2.0f, contrast));
        saveSettings();
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
    
    public float getBrightness() {
        return brightness;
    }
    
    public float getContrast() {
        return contrast;
    }
    
    public int getKeybind(String action) {
        return keybinds.getOrDefault(action, 0);
    }
    
    public Map<String, Integer> getAllKeybinds() {
        return new HashMap<>(keybinds);
    }
    
    public void saveSettings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SETTINGS_FILE))) {
            writer.println("brightness=" + brightness);
            writer.println("contrast=" + contrast);
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
                    if (key.equals("brightness")) {
                        brightness = Float.parseFloat(value);
                    } else if (key.equals("contrast")) {
                        contrast = Float.parseFloat(value);
                    } else if (key.equals("autosave")) {
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
    
    public Color adjustColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        
        // Adjust brightness
        hsb[2] = Math.min(1.0f, hsb[2] * brightness);
        
        // Adjust contrast
        hsb[2] = (hsb[2] - 0.5f) * contrast + 0.5f;
        hsb[2] = Math.max(0.0f, Math.min(1.0f, hsb[2]));
        
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
    
    public void applySettings(Graphics2D g2) {
        // Apply brightness and contrast to the graphics context
        // This is a simple implementation - you might want to use more sophisticated
        // image processing techniques for better results
        float brightnessFactor = brightness - 1.0f;
        float contrastFactor = contrast - 1.0f;
        
        // Create a composite that combines brightness and contrast
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Apply brightness
        if (brightnessFactor != 0) {
            g2.setColor(new Color(
                (int)(brightnessFactor * 255),
                (int)(brightnessFactor * 255),
                (int)(brightnessFactor * 255),
                (int)(Math.abs(brightnessFactor) * 255)
            ));
            g2.fillRect(0, 0, g2.getDeviceConfiguration().getBounds().width,
                       g2.getDeviceConfiguration().getBounds().height);
        }
        
        // Apply contrast
        if (contrastFactor != 0) {
            g2.setColor(new Color(
                (int)(contrastFactor * 255),
                (int)(contrastFactor * 255),
                (int)(contrastFactor * 255),
                (int)(Math.abs(contrastFactor) * 255)
            ));
            g2.fillRect(0, 0, g2.getDeviceConfiguration().getBounds().width,
                       g2.getDeviceConfiguration().getBounds().height);
        }
        
        g2.setComposite(originalComposite);
    }
} 