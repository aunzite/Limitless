package main;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class AudioManager {
    private static AudioManager instance;
    private Clip mainMenuMusic;
    private Clip gameOverMusic;
    private Clip bossFightMusic;
    private Clip area1Music;
    private Clip area2Music;
    private Clip area3Music;
    private Clip mainAreaMusic;  // New field for main area music
    private Clip currentMusic;
    private float volume = 0.2f; // Default volume (0.0 to 1.0)
    
    private AudioManager() {
        loadMusic();
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    private void loadMusic() {
        try {
            // Load main menu music
            File mainMenuFile = new File("res/audio/main_menu.wav");
            if (mainMenuFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(mainMenuFile);
                mainMenuMusic = AudioSystem.getClip();
                mainMenuMusic.open(audioIn);
            }
            
            // Load main area music
            File mainAreaFile = new File("res/audio/area_1.wav");  // Updated file name
            if (mainAreaFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(mainAreaFile);
                mainAreaMusic = AudioSystem.getClip();
                mainAreaMusic.open(audioIn);
            }
            
            // Load other music files as needed
            // Example for game over music:
            File gameOverFile = new File("res/audio/game_over.wav");
            if (gameOverFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(gameOverFile);
                gameOverMusic = AudioSystem.getClip();
                gameOverMusic.open(audioIn);
            }
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // Handle error silently
        }
    }
    
    public void playMainMenuMusic() {
        playMusic(mainMenuMusic);
    }
    
    public void playMainAreaMusic() {
        playMusic(mainAreaMusic);
    }
    
    public void playGameOverMusic() {
        playMusic(gameOverMusic);
    }
    
    public void playBossFightMusic() {
        playMusic(bossFightMusic);
    }
    
    public void playArea1Music() {
        playMusic(area1Music);
    }
    
    public void playArea2Music() {
        playMusic(area2Music);
    }
    
    public void playArea3Music() {
        playMusic(area3Music);
    }
    
    private void playMusic(Clip clip) {
        if (clip == null) {
            return;
        }
        
        try {
            // Stop current music if playing
            if (currentMusic != null) {
                currentMusic.stop();
            }
            
            // Play new music
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            currentMusic = clip;
            
            // Set volume
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
            
        } catch (Exception e) {
            // Handle error silently
        }
    }
    
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }
    
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentMusic != null) {
            FloatControl gainControl = (FloatControl) currentMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
    
    public float getVolume() {
        return volume;
    }
} 