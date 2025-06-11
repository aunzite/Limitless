/////////////////////////////////////////////////////////////////////////////
// Limitless
// AudioManager.java
// 
// Description: Manages all game audio including:
// - Background music
// - Sound effects
// - Volume control
// - Audio state persistence
/////////////////////////////////////////////////////////////////////////////

package main;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import java.util.Map;

// Singleton class that manages all game audio
public class AudioManager {
    // Singleton instance
    private static AudioManager instance;
    
    // Audio settings
    private float musicVolume = 0.5f;
    private boolean musicEnabled = true;
    
    // Audio clips
    private Clip mainMenuMusic;
    private Clip gameOverMusic;
    private Clip bossFightMusic;
    private Clip mainAreaMusic;  // New field for main area music
    private Clip currentMusic;
    
    // Audio file paths
    private static final String MAIN_MENU_MUSIC = "res/audio/main_menu.wav";
    private static final String BOSS_FIGHT_MUSIC = "res/audio/boss_fight.wav";
    private static final String DEFEAT_MUSIC = "res/audio/defeat.wav";
    
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
            File mainMenuFile = new File(MAIN_MENU_MUSIC);
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
            File gameOverFile = new File(DEFEAT_MUSIC);
            if (gameOverFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(gameOverFile);
                gameOverMusic = AudioSystem.getClip();
                gameOverMusic.open(audioIn);
            }
            
            // Load boss fight music
            File bossFightFile = new File(BOSS_FIGHT_MUSIC);
            if (bossFightFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bossFightFile);
                bossFightMusic = AudioSystem.getClip();
                bossFightMusic.open(audioIn);
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
            float dB = (float) (Math.log(musicVolume) / Math.log(10.0) * 20.0);
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
    
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentMusic != null) {
            FloatControl gainControl = (FloatControl) currentMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(musicVolume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
} 