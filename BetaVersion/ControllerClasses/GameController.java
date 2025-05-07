package ControllerClasses;

import javax.swing.*;

import MainClass.Main;
import ModelClasses.Card;
import ModelClasses.GameModel;
import ModelClasses.ScoreManager;
import ModelClasses.SoundManager;
import ViewClasses.GameView;

import javax.swing.*;
import java.awt.event.*;

public class GameController {
    private GameModel model;
    private GameView view;
    private ScoreManager scoreManager;
    private SoundManager soundManager;
    private Timer gameTimer;
    private Timer flipTimer;
    private String playerName;
    private int firstCardIndex = -1;

    public GameController(GameModel model, GameView view, ScoreManager scoreManager, 
                        SoundManager soundManager, String playerName) {
        this.model = model;
        this.view = view;
        this.scoreManager = scoreManager;
        this.soundManager = soundManager;
        this.playerName = playerName;
        
        initializeGame();
        setupEventHandlers();
    }
    
    private void initializeGame() {
        model.initializeGame();
        view.initializeGameBoard(model.getGridSize());
        
        for (int i = 0; i < model.getCards().size(); i++) {
            final int index = i;
            view.setCardListener(index, e -> handleCardClick(index));
            view.updateCard(index, model.getCards().get(index));
        }
        
        view.setBackButtonListener(e -> returnToMenu());
        updateStatus();
        startGameTimer();
    }
    
    private void setupEventHandlers() {
        // All event handlers are set up in initializeGame()
    }
    
    private void handleCardClick(int index) {
        // Don't process if game isn't active or card is already matched
        if (!model.isGameActive() || model.getCards().get(index).isMatched()) {
            return;
        }
        
        // Don't process if we're waiting for cards to flip back
        if (flipTimer != null && flipTimer.isRunning()) {
            return;
        }
        
        // Flip the card
        model.getCards().get(index).setFlipped(true);
        view.updateCard(index, model.getCards().get(index));
        
        if (firstCardIndex == -1) {
            // First card selected
            firstCardIndex = index;
        } else {
            // Second card selected
            model.setAttempts(model.getAttempts() + 1);
            int secondCardIndex = index;
            
            if (cardsMatch(firstCardIndex, secondCardIndex)) {
                // Cards match
                soundManager.playMatchSound();
                handleMatch(firstCardIndex, secondCardIndex);
            } else {
                // Cards don't match
                soundManager.playMismatchSound();
                flipNonMatchingCards(firstCardIndex, secondCardIndex);
            }
            firstCardIndex = -1;
        }
        
        updateStatus();
    }
    
    private boolean cardsMatch(int index1, int index2) {
        return model.getCards().get(index1).getSymbol()
               .equals(model.getCards().get(index2).getSymbol());
    }
    
    private void handleMatch(int index1, int index2) {
        model.getCards().get(index1).setMatched(true);
        model.getCards().get(index2).setMatched(true);
        model.setPairsFound(model.getPairsFound() + 1);
        
        view.updateCard(index1, model.getCards().get(index1));
        view.updateCard(index2, model.getCards().get(index2));
        
        // Check for game completion
        if (model.getPairsFound() == (model.getGridSize() * model.getGridSize()) / 2) {
            endGame(true);
        }
    }
    
    private void flipNonMatchingCards(int index1, int index2) {
        model.setGameActive(false); // Temporarily disable input
        
        flipTimer = new Timer(1000, e -> {
            model.getCards().get(index1).setFlipped(false);
            model.getCards().get(index2).setFlipped(false);
            
            view.updateCard(index1, model.getCards().get(index1));
            view.updateCard(index2, model.getCards().get(index2));
            
            model.setGameActive(true); // Re-enable input
            flipTimer.stop();
        });
        
        flipTimer.setRepeats(false);
        flipTimer.start();
    }
    
    private void updateStatus() {
        String status = String.format("%s | %s | Attempts: %d | Pairs: %d/%d",
            playerName,
            model.getDifficultyText(),
            model.getAttempts(),
            model.getPairsFound(),
            (model.getGridSize() * model.getGridSize()) / 2);
        view.updateStatus(status);
    }
    
    private void startGameTimer() {
        gameTimer = new Timer(1000, e -> {
            model.setTimeLeft(model.getTimeLeft() - 1);
            String timeText = String.format("Time: %02d:%02d", 
                model.getTimeLeft() / 60, model.getTimeLeft() % 60);
            view.updateTimer(timeText, model.getTimeLeft() <= 30);
            
            if (model.getTimeLeft() <= 0) {
                endGame(false);
            }
        });
        
        gameTimer.start();
    }
    
    private void endGame(boolean won) {
        gameTimer.stop();
        model.setGameActive(false);
        
        if (won) {
            soundManager.playWinSound();
            String timeLeft = String.format("%02d:%02d", 
                model.getTimeLeft() / 60, model.getTimeLeft() % 60);
            scoreManager.addScore(playerName, model.getDifficultyText(), 
                model.getAttempts(), timeLeft);
            
            String message = String.format("Congratulations %s! You won in %d attempts!\n", 
                playerName, model.getAttempts());
            message += String.format("With %s remaining!", timeLeft);
            view.showWinMessage(message);
        } else {
            view.showGameOverMessage(String.format(
                "Time's up! You found %d of %d pairs.",
                model.getPairsFound(), (model.getGridSize() * model.getGridSize()) / 2));
        }
        
        returnToMenu();
    }
    
    private void returnToMenu() {
        view.hide();
        Main.showMenuView();
    }
}