package ModelClasses;

import java.io.*;
import java.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ScoreManager {
    private static final String SCORES_FILE = "highscores.txt";
    private List<String> highScores;
    
    public ScoreManager() {
        highScores = new ArrayList<>();
        loadHighScores();
    }
    
    public void addScore(String playerName, String difficulty, int attempts, String timeLeft) {
        String scoreEntry = String.format("%s,%s,%d,%s", playerName, difficulty, attempts, timeLeft);
        highScores.add(scoreEntry);
        Collections.sort(highScores, new ScoreComparator());
        if (highScores.size() > 10) {
            highScores = highScores.subList(0, 10);
        }
        saveHighScores();
    }
    
    public List<String> getHighScores() {
        return highScores;
    }
    
    private void loadHighScores() {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), SCORES_FILE);
            if (Files.exists(filePath)) {
                highScores = Files.readAllLines(filePath);
            }
        } catch (IOException e) {
            System.err.println("Error loading scores: " + e.getMessage());
        }
    }
    
    private void saveHighScores() {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), SCORES_FILE);
            Files.write(filePath, highScores, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }
    
    private static class ScoreComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            String[] parts1 = s1.split(",");
            String[] parts2 = s2.split(",");
            
            if (parts1.length < 4 || parts2.length < 4) return 0;
            
            try {
                int attempts1 = Integer.parseInt(parts1[2]);
                int attempts2 = Integer.parseInt(parts2[2]);
                if (attempts1 != attempts2) return Integer.compare(attempts1, attempts2);
                
                String[] time1 = parts1[3].split(":");
                String[] time2 = parts2[3].split(":");
                int min1 = Integer.parseInt(time1[0]);
                int sec1 = Integer.parseInt(time1[1]);
                int min2 = Integer.parseInt(time2[0]);
                int sec2 = Integer.parseInt(time2[1]);
                
                int totalSec1 = min1 * 60 + sec1;
                int totalSec2 = min2 * 60 + sec2;
                
                return Integer.compare(totalSec2, totalSec1);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}