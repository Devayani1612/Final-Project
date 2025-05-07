package ViewClasses;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MenuView {
    private JFrame frame;
    private JTextField playerNameField;
    private JButton startButton;
    private JButton scoreboardButton;
    private JButton soundToggleButton;
    private JButton[] difficultyButtons;
    
    private final Font TITLE_FONT = new Font("Arial Rounded MT Bold", Font.BOLD, 36);
    private final Font BUTTON_FONT = new Font("Arial Rounded MT Bold", Font.PLAIN, 18);
    
    public MenuView() {
        frame = new JFrame("Memory Card Game - Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(240, 240, 240));
        
        initializeMenu();
    }
    
    private void initializeMenu() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(240, 240, 240));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Memory Card Game", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(new Color(50, 100, 150));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        menuPanel.add(titleLabel, BorderLayout.NORTH);

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
        
        difficultyButtons = new JButton[3];
        difficultyButtons[0] = createMenuButton("Easy (4x4)");
        difficultyButtons[1] = createMenuButton("Medium (6x6)");
        difficultyButtons[2] = createMenuButton("Hard (8x8)");
        
        for (JButton button : difficultyButtons) {
            difficultyPanel.add(button);
        }

        // Start Button
        startButton = createMenuButton("START GAME");
        startButton.setEnabled(false);
        startButton.setBackground(new Color(180, 180, 180));

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 0, 50));
        
        // Scoreboard Button
        scoreboardButton = createMenuButton("View Scoreboard");
        bottomPanel.add(scoreboardButton, BorderLayout.WEST);
        
        // Sound Toggle Button
        soundToggleButton = createMenuButton("🔊 Sounds ON");
        bottomPanel.add(soundToggleButton, BorderLayout.EAST);

        // Add components to center panel
        centerPanel.add(namePanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(difficultyPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(startButton);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(bottomPanel);

        menuPanel.add(centerPanel, BorderLayout.CENTER);
        frame.add(menuPanel);
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
    
    public String getPlayerName() {
        return playerNameField.getText().trim();
    }
    
    public void setStartButtonListener(ActionListener listener) {
        startButton.addActionListener(listener);
    }
    
    public void setScoreboardButtonListener(ActionListener listener) {
        scoreboardButton.addActionListener(listener);
    }
    
    public void setDifficultyButtonListener(int index, ActionListener listener) {
        if (index >= 0 && index < difficultyButtons.length) {
            difficultyButtons[index].addActionListener(listener);
        }
    }
    
    public void setSoundToggleListener(ActionListener listener) {
        soundToggleButton.addActionListener(listener);
    }
    
    public void enableStartButton(boolean enabled) {
        startButton.setEnabled(enabled);
        startButton.setBackground(enabled ? new Color(100, 200, 100) : new Color(180, 180, 180));
    }
    
    public void updateSoundButton(boolean soundEnabled) {
        soundToggleButton.setText(soundEnabled ? "🔊 Sounds ON" : "🔇 Sounds OFF");
    }
    
    public void show() {
        frame.setVisible(true);
    }
    
    public void hide() {
        frame.setVisible(false);
    }

    public JButton[] getDifficultyButtons() {
        return difficultyButtons;
    }
}