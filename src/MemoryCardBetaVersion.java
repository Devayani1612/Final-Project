import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.*;

public class MemoryCardBetaVersion extends JFrame {
    // Game Components
    private JPanel gamePanel, menuPanel, scoreboardPanel;
    private JButton startButton, scoreboardButton, backButton, soundToggleButton;
    private JButton[] cards;
    private JLabel statusLabel, timerLabel, titleLabel;
    private JTextField playerNameField;
    
    // Game Settings
    private final String[] CARD_SYMBOLS = {"🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🦁", "🐮", "🐯", "🐷"};
    private int gridSize = 4;
    private final Color MATCHED_COLOR = new Color(100, 255, 100);  // Brighter green
    private final Color TIME_WARNING_COLOR = new Color(255, 80, 80);  // Brighter red
    private final Color CARD_BACK_COLOR = new Color(80, 140, 220);  // Vibrant blue
    private final Color CARD_FRONT_COLOR = new Color(245, 245, 245);  // Off-white
    private final Font TITLE_FONT = new Font("Arial Rounded MT Bold", Font.BOLD, 36);
    private final Font BUTTON_FONT = new Font("Arial Rounded MT Bold", Font.PLAIN, 18);
    private final Font SCORE_FONT = new Font("Consolas", Font.PLAIN, 16);
    
    // Game State
    private int firstCardIndex = -1;
    private int pairsFound = 0;
    private int attempts = 0;
    private Timer flipTimer;
    private Timer gameTimer;
    private int timeLeft = 180;
    private boolean isGameActive = false;
    private boolean isSoundEnabled = true;
    
    // Audio
    private Clip matchSound, mismatchSound, winSound;
    
    // Score system
    private ArrayList<String> highScores = new ArrayList<>();
    private static final String SCORES_FILE = "highscores.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MemoryCardBetaVersion game = new MemoryCardBetaVersion();
            game.setVisible(true);
        });
    }

    public MemoryCardBetaVersion() {
        setupWindow();
        createSounds();
        createMenuScreen();
        createGameScreen();
        createScoreboardScreen();
        loadHighScores();
        showMenu();
    }

    private void setupWindow() {
        setTitle("Memory Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));
    }

    private void createSounds() {
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

    private void createMenuScreen() {
        menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(240, 240, 240));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        titleLabel = new JLabel("Memory Card Game", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(new Color(50, 100, 150));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 240, 240));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Player Name
        JPanel namePanel = new JPanel();
        namePanel.setBackground(new Color(240, 240, 240));
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setFont(BUTTON_FONT);
        playerNameField = new JTextField(15);
        playerNameField.setFont(BUTTON_FONT);
        playerNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        namePanel.add(nameLabel);
        namePanel.add(Box.createHorizontalStrut(10));
        namePanel.add(playerNameField);

        // Difficulty Selection
        JPanel difficultyPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        difficultyPanel.setBorder(BorderFactory.createTitledBorder(
        	    BorderFactory.createLineBorder(new Color(180, 180, 180)),
        	    "Select Difficulty",
        	    TitledBorder.CENTER,
        	    TitledBorder.DEFAULT_POSITION,
        	    BUTTON_FONT
        	));
        difficultyPanel.setBackground(new Color(240, 240, 240));
        
        String[] difficulties = {"Easy (4x4)", "Medium (6x6)", "Hard (8x8)"};
        for (String diff : difficulties) {
            JButton btn = createMenuButton(diff);
            btn.addActionListener(e -> setDifficulty(diff));
            difficultyPanel.add(btn);
        }

        // Start Button
        startButton = createMenuButton("START GAME");
        startButton.setEnabled(false);
        startButton.setBackground(new Color(180, 180, 180));
        startButton.addActionListener(e -> startGame());

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 0, 50));
        
        // Scoreboard Button
        scoreboardButton = createMenuButton("View Scoreboard");
        scoreboardButton.addActionListener(e -> showScoreboard());
        bottomPanel.add(scoreboardButton, BorderLayout.WEST);
        
        // Sound Toggle Button
        soundToggleButton = createMenuButton(isSoundEnabled ? "🔊 Sounds ON" : "🔇 Sounds OFF");
        soundToggleButton.addActionListener(e -> toggleSounds());
        bottomPanel.add(soundToggleButton, BorderLayout.EAST);

        // Add components to center panel
        centerPanel.add(namePanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(difficultyPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(startButton);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(bottomPanel);

        // Add to main panel
        menuPanel.add(titleLabel, BorderLayout.NORTH);
        menuPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(getBackground().brighter());
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.setColor(new Color(100, 100, 100));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        button.setFont(BUTTON_FONT);
        button.setBackground(new Color(200, 220, 255));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        return button;
    }

    private void createGameScreen() {
        gamePanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                int cardSize = gridSize <= 4 ? 120 : gridSize == 6 ? 90 : 70;
                int totalWidth = gridSize * cardSize + (gridSize-1) * 10;
                int totalHeight = gridSize * cardSize + (gridSize-1) * 10;
                return new Dimension(totalWidth, totalHeight);
            }
        };
        gamePanel.setBackground(new Color(60, 60, 60));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Status Panel
        JPanel statusPanel = new JPanel(new GridLayout(1, 2));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        statusPanel.setBackground(new Color(70, 70, 70));
        
        statusLabel = new JLabel(" ", JLabel.LEFT);
        statusLabel.setFont(BUTTON_FONT);
        statusLabel.setForeground(Color.WHITE);
        
        timerLabel = new JLabel("Time: 03:00", JLabel.RIGHT);
        timerLabel.setFont(BUTTON_FONT);
        timerLabel.setForeground(Color.WHITE);
        
        statusPanel.add(statusLabel);
        statusPanel.add(timerLabel);
        
        add(gamePanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void createScoreboardScreen() {
        scoreboardPanel = new JPanel(new BorderLayout());
        scoreboardPanel.setBackground(new Color(240, 240, 240));
        scoreboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("High Scores", JLabel.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(50, 100, 150));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Scores Area
        JTextArea scoresArea = new JTextArea();
        scoresArea.setEditable(false);
        scoresArea.setFont(SCORE_FONT);
        scoresArea.setBackground(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(scoresArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        updateScoresDisplay(scoresArea);

        // Back Button
        backButton = createMenuButton("BACK TO MENU");
        backButton.addActionListener(e -> showMenu());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.add(backButton);

        scoreboardPanel.add(title, BorderLayout.NORTH);
        scoreboardPanel.add(scrollPane, BorderLayout.CENTER);
        scoreboardPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateScoresDisplay(JTextArea scoresArea) {
        StringBuilder sb = new StringBuilder();
        if (highScores.isEmpty()) {
            sb.append("No high scores yet!\nPlay a game to see your scores here.");
        } else {
            sb.append(String.format("%-20s %-10s %-10s %-10s\n", "Player", "Difficulty", "Attempts", "Time Left"));
            sb.append("------------------------------------------------\n");
            for (String score : highScores) {
                String[] parts = score.split(",");
                if (parts.length >= 4) {
                    sb.append(String.format("%-20s %-10s %-10s %-10s\n", 
                        parts[0], parts[1], parts[2], parts[3]));
                }
            }
        }
        scoresArea.setText(sb.toString());
        scoresArea.setCaretPosition(0);
    }

    private void showMenu() {
        getContentPane().removeAll();
        add(menuPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private void showScoreboard() {
        loadHighScores();
        
        // Find the JTextArea in the scoreboardPanel and update it
        Component[] components = scoreboardPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                JViewport viewport = scrollPane.getViewport();
                Component view = viewport.getView();
                if (view instanceof JTextArea) {
                    updateScoresDisplay((JTextArea) view);
                    break;
                }
            }
        }
        
        getContentPane().removeAll();
        add(scoreboardPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void setDifficulty(String difficulty) {
        gridSize = Integer.parseInt(difficulty.substring(
            difficulty.indexOf('(') + 1, 
            difficulty.indexOf('x')
        ));
        startButton.setEnabled(true);
        startButton.setBackground(new Color(100, 200, 100));
    }

    private void startGame() {
        firstCardIndex = -1;
        pairsFound = 0;
        attempts = 0;
        timeLeft = 180;
        isGameActive = true;
        
        gamePanel.removeAll();
        gamePanel.setLayout(new GridLayout(gridSize, gridSize, 10, 10));

        // Create card pairs
        int pairsNeeded = (gridSize * gridSize) / 2;
        ArrayList<Integer> cardValues = new ArrayList<>();
        for (int i = 0; i < pairsNeeded; i++) {
            cardValues.add(i % CARD_SYMBOLS.length);
            cardValues.add(i % CARD_SYMBOLS.length);
        }
        Collections.shuffle(cardValues);
        
        // Create cards
        cards = new JButton[gridSize * gridSize];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = createCard(cardValues.get(i));
            gamePanel.add(cards[i]);
        }

        updateStatus();
        startGameTimer();

        getContentPane().removeAll();
        add(gamePanel, BorderLayout.CENTER);
        
        // Re-add status panel
        JPanel statusPanel = new JPanel(new GridLayout(1, 2));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        statusPanel.setBackground(new Color(70, 70, 70));
        
        statusLabel = new JLabel(" ", JLabel.LEFT);
        statusLabel.setFont(BUTTON_FONT);
        statusLabel.setForeground(Color.WHITE);
        
        timerLabel = new JLabel(String.format("Time: %02d:%02d", timeLeft / 60, timeLeft % 60), JLabel.RIGHT);
        timerLabel.setFont(BUTTON_FONT);
        timerLabel.setForeground(Color.WHITE);
        
        statusPanel.add(statusLabel);
        statusPanel.add(timerLabel);
        
        add(statusPanel, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }

    private JButton createCard(int symbolIndex) {
        JButton card = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (!isEnabled()) {
                    g2.setColor(MATCHED_COLOR);
                } else if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(getBackground().brighter());
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                g2.setColor(new Color(100, 100, 100));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        int cardSize = gridSize <= 4 ? 120 : gridSize == 6 ? 90 : 70;
        card.setPreferredSize(new Dimension(cardSize, cardSize));
        card.setMinimumSize(new Dimension(cardSize, cardSize));
        card.setMaximumSize(new Dimension(cardSize, cardSize));
        card.setActionCommand(String.valueOf(symbolIndex));
        
        // Card back (hidden state)
        card.setBackground(CARD_BACK_COLOR);
        card.setText("");
        card.setFont(getCardFont());
        card.setForeground(Color.WHITE);
        
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder());
        card.setContentAreaFilled(false);
        card.addActionListener(e -> handleCardClick(card, symbolIndex));
        return card;
    }

    private Font getCardFont() {
        int size;
        if (gridSize <= 4) size = 36;
        else if (gridSize == 6) size = 28;
        else size = 22;
        return new Font("Segoe UI Emoji", Font.PLAIN, size);
    }

    private void handleCardClick(JButton card, int symbolIndex) {
        if ((flipTimer != null && flipTimer.isRunning()) || !card.isEnabled()) {
            return;
        }

        // Flip card to show front
        card.setBackground(CARD_FRONT_COLOR);
        card.setFont(getCardFont());
        card.setText(CARD_SYMBOLS[symbolIndex]);
        card.setForeground(Color.BLACK);

        if (firstCardIndex == -1) {
            firstCardIndex = findCardIndex(card);
        } else {
            attempts++;
            int secondCardIndex = findCardIndex(card);
            
            if (cardsMatch(firstCardIndex, secondCardIndex)) {
                if (isSoundEnabled) playSound(matchSound);
                handleMatch(firstCardIndex, secondCardIndex);
            } else {
                if (isSoundEnabled) playSound(mismatchSound);
                flipNonMatchingCards(firstCardIndex, secondCardIndex);
            }
            firstCardIndex = -1;
            updateStatus();
        }
    }

    private int findCardIndex(JButton card) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == card) return i;
        }
        return -1;
    }

    private boolean cardsMatch(int index1, int index2) {
        return cards[index1].getActionCommand().equals(cards[index2].getActionCommand());
    }

    private void handleMatch(int index1, int index2) {
        pairsFound++;
        cards[index1].setEnabled(false);
        cards[index2].setEnabled(false);
        cards[index1].setBackground(MATCHED_COLOR);
        cards[index2].setBackground(MATCHED_COLOR);
        
        if (pairsFound == cards.length / 2) {
            endGame(true);
        }
    }

    private void flipNonMatchingCards(int index1, int index2) {
        flipTimer = new Timer(1000, e -> {
            cards[index1].setBackground(CARD_BACK_COLOR);
            cards[index1].setText("");
            cards[index1].setFont(getCardFont());
            cards[index1].setForeground(Color.WHITE);
            
            cards[index2].setBackground(CARD_BACK_COLOR);
            cards[index2].setText("");
            cards[index2].setFont(getCardFont());
            cards[index2].setForeground(Color.WHITE);
            
            flipTimer.stop();
        });
        flipTimer.setRepeats(false);
        flipTimer.start();
    }

    private void startGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        timerLabel.setForeground(Color.WHITE);
        
        gameTimer = new Timer(1000, e -> {
            if (!isGameActive) {
                gameTimer.stop();
                return;
            }
            
            timeLeft--;
            updateTimerDisplay();
            
            if (timeLeft <= 30) {
                timerLabel.setForeground(TIME_WARNING_COLOR);
            }
            
            if (timeLeft <= 0) {
                gameTimer.stop();
                endGame(false);
            }
        });
        
        gameTimer.start();
    }

    private void updateTimerDisplay() {
        timerLabel.setText(String.format("Time: %02d:%02d", timeLeft / 60, timeLeft % 60));
    }

    private void updateStatus() {
        String playerName = playerNameField.getText().trim();
        if (playerName.isEmpty()) {
            playerName = "Player";
        }
        statusLabel.setText(String.format(
            "%s | %s | Attempts: %d | Pairs: %d/%d",
            playerName,
            getDifficultyText(),
            attempts,
            pairsFound,
            cards.length / 2
        ));
    }

    private String getDifficultyText() {
        switch (gridSize) {
            case 4: return "Easy";
            case 6: return "Medium";
            case 8: return "Hard";
            default: return "Custom";
        }
    }

    private void endGame(boolean won) {
        isGameActive = false;
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        if (won) {
            if (isSoundEnabled) playSound(winSound);
            saveScore();
            showWinMessage();
        } else {
            JOptionPane.showMessageDialog(this,
                "Time's up! You found " + pairsFound + " of " + (cards.length/2) + " pairs.",
                "Game Over",
                JOptionPane.ERROR_MESSAGE);
            showMenu();
        }
    }

    private void showWinMessage() {
        String playerName = playerNameField.getText().trim();
        if (playerName.isEmpty()) {
            playerName = "Player";
        }
        String message = String.format("Congratulations %s! You won in %d attempts!", playerName, attempts);
        message += String.format("\nWith %02d:%02d remaining!", timeLeft / 60, timeLeft % 60);
        
        JOptionPane.showMessageDialog(this,
            message,
            "You Win!",
            JOptionPane.INFORMATION_MESSAGE);
        showMenu();
    }

    private void toggleSounds() {
        isSoundEnabled = !isSoundEnabled;
        soundToggleButton.setText(isSoundEnabled ? "🔊 Sounds ON" : "🔇 Sounds OFF");
    }	

    private void playSound(Clip sound) {
        if (isSoundEnabled && sound != null) {
            sound.setFramePosition(0);
            sound.start();
        }
    }

    private void saveScore() {
        String playerName = playerNameField.getText().trim();
        if (playerName.isEmpty()) {
            playerName = "Player";
        }
        
        String difficulty = getDifficultyText();
        String timeLeftStr = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60);
        
        String scoreEntry = String.format("%s,%s,%d,%s", 
            playerName, difficulty, attempts, timeLeftStr);
        
        // Load current scores
        loadHighScores();
        
        // Add new score
        highScores.add(scoreEntry);
        
        // Sort and keep top 10
        Collections.sort(highScores, new ScoreComparator());
        if (highScores.size() > 10) {
            highScores = new ArrayList<>(highScores.subList(0, 10));
        }
        
        // Save to file
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), SCORES_FILE);
            Files.write(filePath, highScores, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
            e.printStackTrace();
        }
        
        updateScoreboardDisplay();
    }
    
    private void updateScoreboardDisplay() {
        Component[] components = scoreboardPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                JViewport viewport = scrollPane.getViewport();
                Component view = viewport.getView();
                if (view instanceof JTextArea) {
                    updateScoresDisplay((JTextArea) view);
                    break;
                }
            }
        }
    }

    private class ScoreComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            String[] parts1 = s1.split(",");
            String[] parts2 = s2.split(",");
            
            if (parts1.length < 4 || parts2.length < 4) {
                return 0;
            }
            
            try {
                // First compare by attempts (lower is better)
                int attempts1 = Integer.parseInt(parts1[2]);
                int attempts2 = Integer.parseInt(parts2[2]);
                if (attempts1 != attempts2) {
                    return Integer.compare(attempts1, attempts2);
                }
                
                // Then compare by time left (more is better)
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

    private void loadHighScores() {
        highScores = new ArrayList<>();
        Path filePath = Paths.get(System.getProperty("user.dir"), SCORES_FILE);
        if (Files.exists(filePath)) {
            try {
                highScores = new ArrayList<>(Files.readAllLines(filePath));
            } catch (IOException e) {
                System.err.println("Error loading scores: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}