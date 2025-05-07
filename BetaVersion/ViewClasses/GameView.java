package ViewClasses;

import javax.swing.*;

import ModelClasses.Card;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameView {
    private JFrame frame;
    private JPanel gamePanel;
    private JLabel statusLabel, timerLabel;
    private JButton[] cardButtons;
    private JButton backButton;
    
    private final Color MATCHED_COLOR = new Color(100, 255, 100);
    private final Color TIME_WARNING_COLOR = new Color(255, 80, 80);
    private final Color CARD_BACK_COLOR = new Color(80, 140, 220);
    private final Color CARD_FRONT_COLOR = new Color(245, 245, 245);
    private final Font BUTTON_FONT = new Font("Arial Rounded MT Bold", Font.PLAIN, 18);
    
    public GameView() {
        frame = new JFrame("Memory Card Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(240, 240, 240));
        
        initializeGamePanel();
        initializeStatusPanel();
    }
    
    private void initializeGamePanel() {
        gamePanel = new JPanel();
        gamePanel.setBackground(new Color(60, 60, 60));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.add(gamePanel, BorderLayout.CENTER);
    }
    
    private void initializeStatusPanel() {
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
        
        frame.add(statusPanel, BorderLayout.SOUTH);
    }
    
    public void initializeGameBoard(int gridSize) {
        gamePanel.removeAll();
        gamePanel.setLayout(new GridLayout(gridSize, gridSize, 10, 10));
        
        int cardSize = gridSize <= 4 ? 120 : gridSize == 6 ? 90 : 70;
        cardButtons = new JButton[gridSize * gridSize];
        
        for (int i = 0; i < cardButtons.length; i++) {
            cardButtons[i] = createCardButton(cardSize, gridSize);
            gamePanel.add(cardButtons[i]);
        }
        
        frame.revalidate();
        frame.repaint();
    }
    
    private JButton createCardButton(int size, int gridSize) {
        JButton button = new JButton() {
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
        
        button.setPreferredSize(new Dimension(size, size));
        button.setMinimumSize(new Dimension(size, size));
        button.setMaximumSize(new Dimension(size, size));
        button.setBackground(CARD_BACK_COLOR);
        button.setFont(getCardFont(gridSize));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    private Font getCardFont(int gridSize) {
        int size;
        if (gridSize <= 4) size = 36;
        else if (gridSize == 6) size = 28;
        else size = 22;
        return new Font("Segoe UI Emoji", Font.PLAIN, size);
    }
    
    public void updateCard(int index, Card card) {
        if (card.isFlipped() || card.isMatched()) {
            cardButtons[index].setText(card.getSymbol());
            cardButtons[index].setBackground(card.isMatched() ? MATCHED_COLOR : CARD_FRONT_COLOR);
            cardButtons[index].setForeground(Color.BLACK);
            cardButtons[index].setEnabled(!card.isMatched());
        } else {
            cardButtons[index].setText("");
            cardButtons[index].setBackground(CARD_BACK_COLOR);
            cardButtons[index].setForeground(Color.WHITE);
        }
    }
    
    public void updateStatus(String status) {
        statusLabel.setText(status);
    }
    
    public void updateTimer(String time, boolean warning) {
        timerLabel.setText(time);
        timerLabel.setForeground(warning ? TIME_WARNING_COLOR : Color.WHITE);
    }
    
    public void setCardListener(int index, ActionListener listener) {
        cardButtons[index].addActionListener(listener);
    }
    
    public void setBackButtonListener(ActionListener listener) {
        if (backButton == null) {
            backButton = new JButton("Back to Menu");
            backButton.setFont(BUTTON_FONT);
            frame.add(backButton, BorderLayout.NORTH);
            frame.revalidate();
        }
        backButton.addActionListener(listener);
    }
    
    public void showWinMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "You Win!", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showGameOverMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Game Over", JOptionPane.ERROR_MESSAGE);
    }
    
    public void show() {
        frame.setVisible(true);
    }
    
    public void hide() {
        frame.setVisible(false);
    }
}