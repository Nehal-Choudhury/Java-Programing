import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Problem: Create a simple Tic-Tac-Toe game with a GUI.
// The game should allow two players to take turns marking cells on a 3x3 grid.
// It should detect wins (three in a row, column, or diagonal) and draws.
// A "Reset" button should be available to start a new game.

// Key Concepts:
// - Java Swing for GUI (JFrame, JPanel, JButton, JLabel)
// - 2D array to represent the game board
// - Game logic for turns, win conditions, and draw detection
// - ActionListener for button clicks

public class TicTacToeGame extends JFrame implements ActionListener {

    private JButton[][] buttons; // 3x3 grid of buttons for the game board
    private JLabel statusLabel;  // Label to display game status (current player, winner, draw)
    private boolean playerXTurn; // True if it's Player X's turn, false for Player O
    private int movesMade;       // Counter for moves made to detect draws

    // Constructor
    public TicTacToeGame() {
        setTitle("Tic-Tac-Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450); // Slightly taller to accommodate status label
        setLocationRelativeTo(null); // Center the window

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Status Label (North)
        statusLabel = new JLabel("Player X's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(statusLabel, BorderLayout.NORTH);

        // Game Board Panel (Center)
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3, 5, 5)); // 3x3 grid with gaps
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(boardPanel, BorderLayout.CENTER);

        buttons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton(""); // Empty button initially
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(this); // Add action listener to each button
                buttons[i][j].setBackground(new Color(240, 240, 240)); // Light background
                boardPanel.add(buttons[i][j]);
            }
        }

        // Reset Button Panel (South)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton resetButton = new JButton("Reset Game");
        resetButton.setFont(new Font("Arial", Font.BOLD, 20));
        resetButton.addActionListener(e -> resetGame()); // Lambda for reset action
        resetButton.setBackground(new Color(100, 149, 237)); // CornflowerBlue
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.SOUTH);

        initializeGame(); // Set up initial game state
    }

    // Initialize/Reset game state
    private void initializeGame() {
        playerXTurn = true;
        movesMade = 0;
        statusLabel.setText("Player X's Turn");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText(""); // Clear button text
                buttons[i][j].setEnabled(true); // Enable buttons
                buttons[i][j].setBackground(new Color(240, 240, 240)); // Reset background
            }
        }
    }

    // Reset game (called by reset button)
    private void resetGame() {
        initializeGame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource(); // Get the button that was clicked

        // If the button is already marked or game is over, do nothing
        if (!clickedButton.getText().isEmpty()) {
            return;
        }

        // Set the button text based on current player
        if (playerXTurn) {
            clickedButton.setText("X");
            clickedButton.setForeground(new Color(0, 128, 0)); // Green for X
        } else {
            clickedButton.setText("O");
            clickedButton.setForeground(new Color(255, 69, 0)); // OrangeRed for O
        }
        clickedButton.setEnabled(false); // Disable the button after a move
        movesMade++;

        // Check for win or draw
        if (checkWin()) {
            String winner = playerXTurn ? "X" : "O";
            statusLabel.setText("Player " + winner + " Wins!");
            disableAllButtons(); // End the game
        } else if (movesMade == 9) { // All cells filled, no winner
            statusLabel.setText("It's a Draw!");
            disableAllButtons();
        } else {
            // Switch turns
            playerXTurn = !playerXTurn;
            statusLabel.setText("Player " + (playerXTurn ? "X" : "O") + "'s Turn");
        }
    }

    // Check for a win condition
    private boolean checkWin() {
        String currentMark = playerXTurn ? "X" : "O";

        // Check rows
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(currentMark) &&
                buttons[i][1].getText().equals(currentMark) &&
                buttons[i][2].getText().equals(currentMark)) {
                highlightWinningButtons(buttons[i][0], buttons[i][1], buttons[i][2]);
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (buttons[0][j].getText().equals(currentMark) &&
                buttons[1][j].getText().equals(currentMark) &&
                buttons[2][j].getText().equals(currentMark)) {
                highlightWinningButtons(buttons[0][j], buttons[1][j], buttons[2][j]);
                return true;
            }
        }

        // Check diagonals
        if (buttons[0][0].getText().equals(currentMark) &&
            buttons[1][1].getText().equals(currentMark) &&
            buttons[2][2].getText().equals(currentMark)) {
            highlightWinningButtons(buttons[0][0], buttons[1][1], buttons[2][2]);
            return true;
        }
        if (buttons[0][2].getText().equals(currentMark) &&
            buttons[1][1].getText().equals(currentMark) &&
            buttons[2][0].getText().equals(currentMark)) {
            highlightWinningButtons(buttons[0][2], buttons[1][1], buttons[2][0]);
            return true;
        }

        return false;
    }

    // Highlight the winning combination
    private void highlightWinningButtons(JButton b1, JButton b2, JButton b3) {
        Color winColor = new Color(144, 238, 144); // LightGreen
        b1.setBackground(winColor);
        b2.setBackground(winColor);
        b3.setBackground(winColor);
    }

    // Disable all buttons after game ends
    private void disableAllButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TicTacToeGame().setVisible(true);
        });
    }
}
