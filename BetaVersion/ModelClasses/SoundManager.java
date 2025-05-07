package ModelClasses;

import javax.sound.sampled.*;
import java.io.*;

public class SoundManager {
    private Clip matchSound, mismatchSound, winSound;
    private boolean soundEnabled = true;
    
    public SoundManager() {
        try {
            matchSound = createToneClip(880, 200);
            mismatchSound = createToneClip(220, 300);
            winSound = createToneSequence(new int[]{660, 784, 880}, new int[]{200, 200, 400});
        } catch (Exception e) {
            System.err.println("Error creating sounds: " + e.getMessage());
        }
    }
    
    private Clip createToneClip(int hz, int msecs) throws LineUnavailableException {
        AudioFormat af = new AudioFormat(44100, 8, 1, true, false);
        Clip clip = AudioSystem.getClip();
        byte[] buffer = new byte[44100 * msecs / 1000];
        
        for (int i = 0; i < buffer.length; i++) {
            double progress = (double)i / buffer.length;
            double angle = i / (44100.0 / hz) * 2.0 * Math.PI;
            buffer[i] = (byte)(Math.sin(angle) * 127.0 * Math.sin(progress * Math.PI));
        }
        
        clip.open(af, buffer, 0, buffer.length);
        return clip;
    }
    
    private Clip createToneSequence(int[] frequencies, int[] durations) throws LineUnavailableException {
        AudioFormat af = new AudioFormat(44100, 8, 1, true, false);
        Clip clip = AudioSystem.getClip();
        
        int totalSamples = 0;
        for (int duration : durations) {
            totalSamples += 44100 * duration / 1000;
        }
        
        byte[] buffer = new byte[totalSamples];
        int pos = 0;
        
        for (int i = 0; i < frequencies.length; i++) {
            int hz = frequencies[i];
            int samples = 44100 * durations[i] / 1000;
            
            for (int j = 0; j < samples; j++) {
                double progress = (double)j / samples;
                double angle = (pos + j) / (44100.0 / hz) * 2.0 * Math.PI;
                buffer[pos + j] = (byte)(Math.sin(angle) * 127.0 * Math.sin(progress * Math.PI));
            }
            pos += samples;
        }
        
        clip.open(af, buffer, 0, buffer.length);
        return clip;
    }
    
    public void playMatchSound() {
        if (soundEnabled && matchSound != null) {
            matchSound.setFramePosition(0);
            matchSound.start();
        }
    }
    
    public void playMismatchSound() {
        if (soundEnabled && mismatchSound != null) {
            mismatchSound.setFramePosition(0);
            mismatchSound.start();
        }
    }
    
    public void playWinSound() {
        if (soundEnabled && winSound != null) {
            winSound.setFramePosition(0);
            winSound.start();
        }
    }
    
    public void toggleSound() {
        soundEnabled = !soundEnabled;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}
