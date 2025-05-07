package ControllerClasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ModelClasses.GameModel;
import ModelClasses.ScoreManager;
import ModelClasses.SoundManager;
import ViewClasses.GameView;
import ViewClasses.MenuView;
import ViewClasses.ScoreboardView;

public class MenuController {
    private MenuView view;
    private ScoreManager scoreManager;
    private SoundManager soundManager;
    private ScoreboardView scoreboardView;
    
    public MenuController(MenuView view, ScoreManager scoreManager, SoundManager soundManager) {
        this.view = view;
        this.scoreManager = scoreManager;
        this.soundManager = soundManager;
        this.scoreboardView = new ScoreboardView();
        
        setupEventHandlers();
    }
    
    private void setupEventHandlers() {
        // Set difficulty button listeners
        view.setDifficultyButtonListener(0, e -> setDifficulty(4));
        view.setDifficultyButtonListener(1, e -> setDifficulty(6));
        view.setDifficultyButtonListener(2, e -> setDifficulty(8));
        
        view.setStartButtonListener(e -> startGame());
        view.setScoreboardButtonListener(e -> showScoreboard());
        view.setSoundToggleListener(e -> toggleSounds());
        
        scoreboardView.setBackButtonListener(e -> scoreboardView.hide());
    }
    
    private void setDifficulty(int gridSize) {
        view.enableStartButton(true);
    }
    
    private void startGame() {
        String playerName = view.getPlayerName();
        if (playerName.isEmpty()) {
            playerName = "Player";
        }
        
        // Default to easy if no difficulty selected (shouldn't happen as start button is disabled)
        int gridSize = 4;
        
        // Determine which difficulty button was pressed
        for (int i = 0; i < 3; i++) {
            if (view.getDifficultyButtons()[i].getModel().isArmed()) {
                gridSize = (i == 0) ? 4 : (i == 1) ? 6 : 8;
                break;
            }
        }
        
        view.hide();
        
        GameModel model = new GameModel(gridSize);
        GameView gameView = new GameView();
        new GameController(model, gameView, scoreManager, soundManager, playerName);
        gameView.show();
    }
    
    private void showScoreboard() {
        scoreboardView.displayScores(scoreManager.getHighScores());
        scoreboardView.show();
    }
    
    private void toggleSounds() {
        soundManager.toggleSound();
        view.updateSoundButton(soundManager.isSoundEnabled());
    }
}