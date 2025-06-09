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
                System.out.println("Main menu music loaded successfully");
            }
            
            // Load other music files as needed
            // Example for game over music:
            File gameOverFile = new File("res/audio/game_over.wav");
            if (gameOverFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(gameOverFile);
                gameOverMusic = AudioSystem.getClip();
                gameOverMusic.open(audioIn);
                System.out.println("Game over music loaded successfully");
            }
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void playMainMenuMusic() {
        playMusic(mainMenuMusic);
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
            System.err.println("Music clip is null");
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
            
            System.out.println("Music started playing");
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
            System.out.println("Music stopped");
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