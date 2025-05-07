package ModelClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.*;
import javax.swing.Timer;

public class GameModel {
    private List<Card> cards;
    private int firstCardIndex = -1;
    private int pairsFound = 0;
    private int attempts = 0;
    private int timeLeft = 180;
    private boolean gameActive = false;
    private Timer gameTimer;
    private int gridSize;
    
    private final String[] CARD_SYMBOLS = {"🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🦁", "🐮", "🐯", "🐷"};
    
    public GameModel(int gridSize) {
        this.gridSize = gridSize;
    }
    
    public void initializeGame() {
        pairsFound = 0;
        attempts = 0;
        timeLeft = 180;
        gameActive = true;
        firstCardIndex = -1;
        
        List<String> symbols = new ArrayList<>();
        int pairsNeeded = (gridSize * gridSize) / 2;
        
        for (int i = 0; i < pairsNeeded; i++) {
            symbols.add(CARD_SYMBOLS[i % CARD_SYMBOLS.length]);
            symbols.add(CARD_SYMBOLS[i % CARD_SYMBOLS.length]);
        }
        
        Collections.shuffle(symbols);
        cards = new ArrayList<>();
        
        for (String symbol : symbols) {
            cards.add(new Card(symbol));
        }
    }
    
    public List<Card> getCards() { return cards; }
    public int getPairsFound() { return pairsFound; }
    public int getAttempts() { return attempts; }
    public int getTimeLeft() { return timeLeft; }
    public boolean isGameActive() { return gameActive; }
    public int getGridSize() { return gridSize; }
    
    public void selectCard(int index) {
        if (!gameActive || cards.get(index).isMatched() || cards.get(index).isFlipped()) {
            return;
        }
        
        cards.get(index).setFlipped(true);
        
        if (firstCardIndex == -1) {
            firstCardIndex = index;
        } else {
            attempts++;
            if (cards.get(firstCardIndex).getSymbol().equals(cards.get(index).getSymbol())) {
                cards.get(firstCardIndex).setMatched(true);
                cards.get(index).setMatched(true);
                pairsFound++;
                
                if (pairsFound == (gridSize * gridSize) / 2) {
                    gameActive = false;
                    if (gameTimer != null) {
                        gameTimer.stop();
                    }
                }
            } else {
                gameActive = false; // Temporarily disable input
            }
            firstCardIndex = -1;
        }
    }
    
    public void flipNonMatchingCards(int index1, int index2) {
        cards.get(index1).setFlipped(false);
        cards.get(index2).setFlipped(false);
        gameActive = true;
    }
    
    public void setGameTimer(Timer timer) {
        this.gameTimer = timer;
    }
    
    public void decrementTime() {
        timeLeft--;
        if (timeLeft <= 0) {
            gameActive = false;
            if (gameTimer != null) {
                gameTimer.stop();
            }
        }
    }
    
    public String getDifficultyText() {
        switch (gridSize) {
            case 4: return "Easy";
            case 6: return "Medium";
            case 8: return "Hard";
            default: return "Custom";
        }
    }

	public void setAttempts(int i) {
		// TODO Auto-generated method stub
		this.attempts = i;
		
	}

	public void setGameActive(boolean b) {
		// TODO Auto-generated method stub
		this.gameActive=b;
		
	}

	public void setPairsFound(int i) {
		// TODO Auto-generated method stub
		this.pairsFound = i;
		
	}

	public void setTimeLeft(int i) {
		// TODO Auto-generated method stub
		this.timeLeft = i;
		
	}

	

	
}