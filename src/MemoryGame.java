// Import necessary libraries for GUI components, events and other utilities in code
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

//Main game class extending JFrame for the application window
public class MemoryGame extends JFrame {
    // Game Components
	// Main container panels
    private JPanel mainPanel, gamePanel, menuPanel; 
    // This is the buton to start the game
    private JButton startButton;
    // this is the array to hold all card buttons
    private JButton[] cards;
    //label that to used to display game status
    private JLabel statusLabel;
    //text field for player to enter the name
    private JTextField playerNameField;
    
    // Game Settings
    //Emoji symbols for cards
    private final String[] EMOJIS = {"🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🦁", "🐮", "🐯", "🐷"};
    // Default grid size
    private int gridSize = 4; 
    //Blue color for card is in back position.
    private final Color CARD_BACK_COLOR = new Color(70, 130, 200);
    //White color for revealed or flipped cards
    private final Color CARD_FRONT_COLOR = Color.WHITE;
    //green color when the cards matches
    private final Color MATCHED_COLOR = new Color(150, 255, 150);
    
    // Game State
    //Index of the first flipped card i.e.,-1 is none
    private int firstCardIndex = -1;
    // Count of matched pairs
    private int pairsFound = 0;
    // Count of matching attempts
    private int attempts = 0;
    // Timer for card flip animations
    private Timer flipTimer;

    //Constructor to initialize the game
    public MemoryGame() {
    	//Setting up the main window
        setupWindow();
        //Creating the menu screen
        createMenuScreen();
        //Preparing the game screen
        createGameScreen();
        //Showing the menu initially
        showMenu();
    }

    //Method to configure the main window
    private void setupWindow() {
    	//setting the window title
        setTitle("Memory Master");
        //close on X button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Size of the window
        setSize(800, 600);
        //center window on screen
        setLocationRelativeTo(null); 
        //Use BorderLayout for main layout
        setLayout(new BorderLayout());
    }

    //Method to create the menu screen
    private void createMenuScreen() {
    	//Creating menu panel with BorderLayout
        menuPanel = new JPanel(new BorderLayout());
        // we are setting the light gray background
        menuPanel.setBackground(new Color(240, 240, 240));

        //Creating and configuring title label 
        JLabel title = new JLabel("Memory Master", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));

        //creating Center Panel with vertical boxlayout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 240, 240));

        // Panel for player name input
        JPanel namePanel = new JPanel();
        //Adding the label
        namePanel.add(new JLabel("Your Name:"));
        //creating the textfield
        playerNameField = new JTextField(15);
        namePanel.add(playerNameField);

        // Panel for difficulty selection
        //3 rows and 1 column
        JPanel difficultyPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        difficultyPanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));
        
        //Creating the difficulty buttons
        String[] difficulties = {"Easy (4x4)", "Medium (6x6)", "Hard (8x8)"};
        for (String diff : difficulties) {
            JButton btn = createMenuButton(diff);
            btn.addActionListener(e -> setDifficulty(diff));
            difficultyPanel.add(btn);
        }

        //Creating and configuring start button
        startButton = createMenuButton("START GAME");
        //Disabled until difficulty selected
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startGame());

        // Add components to center panel
        centerPanel.add(namePanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(new JLabel("Select Difficulty:", JLabel.CENTER));
        centerPanel.add(difficultyPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(startButton);

        // Add components to menu panel
        menuPanel.add(title, BorderLayout.NORTH);
        menuPanel.add(centerPanel, BorderLayout.CENTER);
    }

    // Helper method to create styled menu buttons
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(new Color(200, 220, 255));
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

    //Method to set difficulty from button text
    private void setDifficulty(String difficulty) {
        gridSize = Integer.parseInt(difficulty.substring(
            difficulty.indexOf('(') + 1, 
            difficulty.indexOf('x')
        ));
        startButton.setEnabled(true);
        startButton.setBackground(new Color(100, 200, 100));
    }

    //  Method to create the game screen (initial setup)
    private void createGameScreen() {
        gamePanel = new JPanel();
        gamePanel.setBackground(new Color(50, 50, 50));
        
        // Status bar
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
    }

    //Method to show the menu screen
    private void showMenu() {
    	//Clear current content
        getContentPane().removeAll();
        // Add menu panel
        add(menuPanel, BorderLayout.CENTER);
        //Refresh layout
        revalidate();
        //Redraw components
        repaint();
    }
    //Method to start the game
    private void startGame() {
        // Reset game state
        firstCardIndex = -1;
        pairsFound = 0;
        attempts = 0;

        // Setup game panel
        // Clear any existing cards
        gamePanel.removeAll();
        gamePanel.setLayout(new GridLayout(gridSize, gridSize, 5, 5));

       
       //Create and add card buttons to panel
        cards = new JButton[gridSize * gridSize];
        String[] cardValues = generateCardValues();
        
        for (int i = 0; i < cards.length; i++) {
            cards[i] = createCard(cardValues[i]);
            gamePanel.add(cards[i]);
        }

        // Update status display
        updateStatus();

       // Show game screen
       // Clear current content
        getContentPane().removeAll();
     // Add game panel
        add(gamePanel, BorderLayout.CENTER);
     // Add status at bottom
        add(statusLabel, BorderLayout.SOUTH);
     // Refresh layout and Redraw components
        revalidate();
        repaint();
    }
    // Method to generate shuffled card values
    private String[] generateCardValues() {
        int pairsNeeded = (gridSize * gridSize) / 2;
        ArrayList<String> values = new ArrayList<>();
        
        // Add pairs of emojis
        for (int i = 0; i < pairsNeeded; i++) {
            String emoji = EMOJIS[i % EMOJIS.length];
            values.add(emoji);
            values.add(emoji);
        }
        
        // Shuffle and convert to array
        Collections.shuffle(values);
        return values.toArray(new String[0]);
    }
    // Method to create a single card button
    private JButton createCard(String value) {
        JButton card = new JButton();
        card.setFont(new Font("Segoe UI Emoji", Font.PLAIN, getCardFontSize()));
        card.setBackground(CARD_BACK_COLOR);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        // Add click handler with card value
        card.addActionListener(e -> handleCardClick(card, value));
        return card;
    }
    // Method to handle card clicks
    private void handleCardClick(JButton card, String value) {
        // Ignore if card is already revealed or animation is playing
        if (!card.getText().isEmpty() || (flipTimer != null && flipTimer.isRunning())) {
            return;
        }

        // Reveal card by showing value and changing color
        card.setText(value);
        card.setBackground(CARD_FRONT_COLOR);

        if (firstCardIndex == -1) {
            // First card of pair-store its index
            firstCardIndex = findCardIndex(card);
        } else {
            // Second card of pair
            attempts++;
            int secondCardIndex = findCardIndex(card);
            
            if (cardsMatch(firstCardIndex, secondCardIndex)) {
                // Match found
                handleMatch(firstCardIndex, secondCardIndex);
            } else {
                // No match - flip back
                flipNonMatchingCards(firstCardIndex, secondCardIndex);
            }
         // Reset for next turn,Update display
            firstCardIndex = -1;
            updateStatus();
        }
    }
    // Helper method to find index of a card
    private int findCardIndex(JButton card) {
        // Search through cards array
        for (int i = 0; i < cards.length; i++) {
        	// Return index when found
            if (cards[i] == card) return i;
        }
        // Not found 
        return -1;
    }
    // Method to check if two cards match
    private boolean cardsMatch(int index1, int index2) {
        return cards[index1].getText().equals(cards[index2].getText());
    }
    // Method to handle matched cards
    private void handleMatch(int index1, int index2) {
        pairsFound++;
        cards[index1].setEnabled(false);
        cards[index2].setEnabled(false);
        cards[index1].setBackground(MATCHED_COLOR);
        cards[index2].setBackground(MATCHED_COLOR);
        
        // Check if all pairs found
        if (pairsFound == cards.length / 2) {
        	// Show victory message
            showWinMessage();
        }
    }
    // Method to flip non-matching cards back
    private void flipNonMatchingCards(int index1, int index2) {
        // Create timer to flip back after 1 second
        flipTimer = new Timer(1000, e -> {
            cards[index1].setText("");
            cards[index2].setText("");
            cards[index1].setBackground(CARD_BACK_COLOR);
            cards[index2].setBackground(CARD_BACK_COLOR);
            flipTimer.stop();
        });
        flipTimer.setRepeats(false);
        flipTimer.start();
    }
    // Method to update status display
    private void updateStatus() {
        // Use "Player" if name field is empty
        String playerName = playerNameField.getText().isEmpty() ? "Player" : playerNameField.getText();
        // Format status text
        statusLabel.setText(String.format(
            "%s | %s | Attempts: %d | Pairs: %d/%d",
            playerName,
            getDifficultyText(),
            attempts,
            pairsFound,
            cards.length / 2
        ));
    }
    // Helper method to get difficulty as text
    private String getDifficultyText() {
        switch (gridSize) {
            case 4: return "Easy";
            case 6: return "Medium";
            case 8: return "Hard";
            default: return "Custom";
        }
    }
    // Method to calculate card font size based on grid size
    private int getCardFontSize() {
        // Larger fonts for smaller grids (60 - 5*size)
        return 60 - (gridSize * 5);
    }
    // Method to show win message
    private void showWinMessage() {
        // Use "Player" if name field is empty
        String playerName = playerNameField.getText().isEmpty() ? "Player" : playerNameField.getText();
        // Show message dialog
        JOptionPane.showMessageDialog(this,
            String.format("Congratulations %s! You won in %d attempts!", playerName, attempts),
            "You Win!",
            JOptionPane.INFORMATION_MESSAGE
        );
     // Return to menu
        showMenu();
    }
    // Main method to launch the game
    public static void main(String[] args) {
        // Using SwingUtilities to ensure thread-safe GUI operations
        SwingUtilities.invokeLater(() -> new MemoryGame().setVisible(true));
    }
}