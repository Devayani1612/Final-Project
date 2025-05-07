package ViewClasses;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class ScoreboardView {
    private JFrame frame;
    private JTextArea scoresArea;
    private JButton backButton;
    
    private final Font TITLE_FONT = new Font("Arial Rounded MT Bold", Font.BOLD, 36);
    private final Font SCORE_FONT = new Font("Consolas", Font.PLAIN, 16);
    
    public ScoreboardView() {
        frame = new JFrame("High Scores");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(240, 240, 240));
        
        initializeUI();
    }
    
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("High Scores", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(new Color(50, 100, 150));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Scores Area
        scoresArea = new JTextArea();
        scoresArea.setEditable(false);
        scoresArea.setFont(SCORE_FONT);
        scoresArea.setBackground(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(scoresArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Back Button
        backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 18));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }
    
    public void displayScores(List<String> scores) {
        StringBuilder sb = new StringBuilder();
        if (scores.isEmpty()) {
            sb.append("No high scores yet!\nPlay a game to see your scores here.");
        } else {
            sb.append(String.format("%-20s %-10s %-10s %-10s\n", "Player", "Difficulty", "Attempts", "Time Left"));
            sb.append("------------------------------------------------\n");
            for (String score : scores) {
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
    
    public void setBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
    
    public void show() {
        frame.setVisible(true);
    }
    
    public void hide() {
        frame.setVisible(false);
    }
}