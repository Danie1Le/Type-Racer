package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import model.GameState;
import model.Quote;

public class TypeRacerGUI {
    private final GameState gameState;
    private final JFrame frame;
    private final JTextArea quoteArea;
    private final JTextArea inputArea;
    private final JButton startButton;
    private final JButton submitButton;
    private final JLabel timerLabel;
    private final JLabel accuracyLabel;
    private final JLabel resultLabel;
    private final JButton[] difficultyButtons;
    private Timer timer;

    public TypeRacerGUI() {
        this.gameState = new GameState();
        
        // Initialize frame
        frame = new JFrame("Type Racer Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        // Initialize components
        quoteArea = createQuoteArea();
        inputArea = createInputArea();
        startButton = createStartButton();
        submitButton = createSubmitButton();
        timerLabel = new JLabel("Time: 0 seconds");
        accuracyLabel = new JLabel("Accuracy: 0%");
        resultLabel = new JLabel("");
        difficultyButtons = new JButton[3];

        setupLayout();
        setupTimer();
    }

    private JTextArea createQuoteArea() {
        JTextArea area = new JTextArea(6, 40);
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 18));
         area.setLineWrap(true);
        area.setWrapStyleWord(true);
         area.setBackground(new Color(250, 250, 250));
         area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return area;
    }

    private JTextArea createInputArea() {
        JTextArea area = new JTextArea(6, 40);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
         area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            
        area.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Start timer on first keypress if game isn't started
                if (!gameState.isGameStarted() && !startButton.isEnabled()) {
                    gameState.startTimer();
                    timer.start();
                }
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    if (submitButton.isEnabled()) {
                        submitButton.doClick();
                    }
                } 
                else if (e.getKeyCode() == KeyEvent.VK_TAB){
                    e.consume();
                    if (startButton.isEnabled()) {
                        startButton.doClick();
                    }
                }
             }
            });
        
        return area;
    }

    private JButton createStartButton() {
        JButton button = new JButton("Start Game (Tab)");
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(92, 184, 92));
        button.setForeground(Color.WHITE);
        
        button.addActionListener(e -> {
            gameState.prepareGame();
            Quote quote = gameState.getCurrentQuote();
            quoteArea.setText(quote.getContent());
            inputArea.setText("");
            startButton.setEnabled(false);
            submitButton.setEnabled(true);
        });
        
        return button;
    }

    private JButton createSubmitButton() {
        JButton button = new JButton("Submit (Enter)");
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(51, 122, 183));
         button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setEnabled(false);
        
        button.addActionListener(e -> {
            gameState.endGame();
            submitButton.setEnabled(false);
            startButton.setEnabled(true);
            timer.stop();
            
            double accuracy = gameState.calculateAccuracy(inputArea.getText());
            resultLabel.setText(String.format("Result: %.1f%% accurate", accuracy * 100));
        });
        
        return button;
    }

    private void setupTimer() {
        timer = new Timer(1000, e -> {
            if (gameState.isGameStarted()) {
                long elapsedSeconds = gameState.getElapsedTime() / 1000;
                timerLabel.setText("Time: " + elapsedSeconds + " seconds");
            }
        });
    }

    private void setupLayout() {
         // create main panel
         JPanel mainPanel = new JPanel();
          mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(220,220,220));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // create side panel
        JPanel sidePanel = createSidePanel();

        // add components to main panel
        addComponentsToMainPanel(mainPanel);

         // create container panel
         JPanel containerPanel = new JPanel(new BorderLayout());
         containerPanel.add(mainPanel, BorderLayout.CENTER);
         containerPanel.add(sidePanel, BorderLayout.EAST);

        frame.getContentPane().add(containerPanel);
         frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createSidePanel(){
        // create side panel for difficulties
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(169,169,169));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidePanel.setPreferredSize(new Dimension(150, 600));

        // add text difficulties
        JLabel difficultyLabel = new JLabel("Select Duration:");
        difficultyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
         difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         sidePanel.add(difficultyLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        createDifficultyButtons(sidePanel);

        return sidePanel;
    }

    private void createDifficultyButtons(JPanel sidePanel) {
        String[] durations = {"5 sec", "10 sec", "15 sec"}; // different difficulty
        Color[] buttonColors = {new Color(0, 255, 0), new Color(255, 255, 0),new Color(255, 0 , 0)}; // make color first is green then yellow then red

        for (int i = 0; i < 3; i++) {
            final int duration = (i + 1) * 5;
            difficultyButtons[i] = new JButton(durations[i]);
             difficultyButtons[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
            difficultyButtons[i].setBackground(buttonColors[i]); // set background color of button
             difficultyButtons[i].setForeground(Color.WHITE);
            difficultyButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            difficultyButtons[i].setMaximumSize(new Dimension(160, 40));
            
            difficultyButtons[i].addActionListener(e -> {
                gameState.setSelectedDuration(duration);
                updateDifficultyButtonStates(duration);
            });
            
            sidePanel.add(difficultyButtons[i]);
            sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // set initial button state
        updateDifficultyButtonStates(10);
    }


    private void addComponentsToMainPanel(JPanel mainPanel){
        // add title
        JLabel titleLabel = new JLabel("Type the following quote:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(quoteArea);
         mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // add user input section
        JLabel inputLabel = new JLabel("Type your answer:");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        inputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         mainPanel.add(inputLabel);
          mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
          mainPanel.add(inputArea);
         mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(220,220,220));
        buttonPanel.add(startButton);
          buttonPanel.add(submitButton);
            mainPanel.add(buttonPanel);
         mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // add stats
            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            statsPanel.setBackground(new Color(220,220,220));
            statsPanel.add(timerLabel);
            statsPanel.add(accuracyLabel);
            mainPanel.add(statsPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(resultLabel);
    }

    private void updateDifficultyButtonStates(int selectedDuration) {
        for (int i = 0; i < difficultyButtons.length; i++) {
            int duration = (i + 1) * 5;
            difficultyButtons[i].setEnabled(duration != selectedDuration);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TypeRacerGUI::new);
    }
}
