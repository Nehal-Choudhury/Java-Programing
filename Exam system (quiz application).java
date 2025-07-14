import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Problem: Create a simple exam/quiz application with a GUI.
// The application will present multiple-choice questions one by one.
// Users can select answers, and at the end, their score will be displayed.

// Key Concepts:
// - Java Swing for GUI (JFrame, JPanel, JLabel, JRadioButton, ButtonGroup, JButton)
// - Data Structure: ArrayList to store Question objects
// - Custom Question class to encapsulate question, options, and correct answer
// - Event handling for navigation (Next/Submit) and answer selection
// - Basic scoring logic and result display

public class ExamSystemApp extends JFrame implements ActionListener {

    private List<Question> questions; // List of all questions
    private int currentQuestionIndex; // Index of the current question being displayed
    private int score;                // User's score

    private JLabel questionLabel;      // Displays the question text
    private JRadioButton[] optionButtons; // Radio buttons for answer options
    private ButtonGroup optionsGroup;   // Groups radio buttons so only one can be selected
    private JButton nextButton;         // Button to go to the next question or submit
    private JButton restartButton;      // Button to restart the quiz

    // Question class to hold question data
    private static class Question {
        String questionText;
        String[] options;
        int correctAnswerIndex; // 0-based index of the correct answer

        public Question(String questionText, String[] options, int correctAnswerIndex) {
            this.questionText = questionText;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }

        public String getQuestionText() { return questionText; }
        public String[] getOptions() { return options; }
        public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    }

    public ExamSystemApp() {
        setTitle("Simple Quiz Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeQuestions(); // Load questions
        initializeGUI();       // Set up GUI components
        displayQuestion();     // Display the first question
    }

    private void initializeQuestions() {
        questions = new ArrayList<>();
        questions.add(new Question("What is the capital of France?",
                new String[]{"Berlin", "Madrid", "Paris", "Rome"}, 2));
        questions.add(new Question("Which planet is known as the Red Planet?",
                new String[]{"Earth", "Mars", "Jupiter", "Venus"}, 1));
        questions.add(new Question("What is 7 * 8?",
                new String[]{"54", "56", "64", "72"}, 1));
        questions.add(new Question("Which data structure uses LIFO principle?",
                new String[]{"Queue", "Array", "Stack", "Linked List"}, 2));
        questions.add(new Question("What is the largest ocean on Earth?",
                new String[]{"Atlantic", "Indian", "Arctic", "Pacific"}, 3));

        // Shuffle questions for a different order each time
        Collections.shuffle(questions);
    }

    private void initializeGUI() {
        currentQuestionIndex = 0;
        score = 0;

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Question Panel (North)
        questionLabel = new JLabel("Question Text Here", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 22));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(questionLabel, BorderLayout.NORTH);

        // Options Panel (Center)
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // 4 options, 1 column
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50)); // Indent options

        optionsGroup = new ButtonGroup();
        optionButtons = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton("Option " + (i + 1));
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 18));
            optionButtons[i].setFocusPainted(false);
            optionsGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }
        mainPanel.add(optionsPanel, BorderLayout.CENTER);

        // Control Panel (South)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        nextButton = new JButton("Next Question");
        nextButton.setFont(new Font("Arial", Font.BOLD, 18));
        nextButton.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(this);
        controlPanel.add(nextButton);

        restartButton = new JButton("Restart Quiz");
        restartButton.setFont(new Font("Arial", Font.BOLD, 18));
        restartButton.setBackground(new Color(70, 130, 180)); // SteelBlue
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.addActionListener(e -> restartQuiz());
        restartButton.setVisible(false); // Hidden initially
        controlPanel.add(restartButton);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            questionLabel.setText("Q" + (currentQuestionIndex + 1) + ": " + q.getQuestionText());
            optionsGroup.clearSelection(); // Clear previous selection

            for (int i = 0; i < q.getOptions().length; i++) {
                optionButtons[i].setText(q.getOptions()[i]);
                optionButtons[i].setVisible(true); // Ensure all options are visible
                optionButtons[i].setEnabled(true); // Enable for selection
            }
            nextButton.setText("Next Question");
            nextButton.setVisible(true);
            restartButton.setVisible(false);
        } else {
            showResult(); // All questions answered
        }
    }

    private void showResult() {
        questionLabel.setText("Quiz Completed!");
        for (JRadioButton btn : optionButtons) {
            btn.setVisible(false); // Hide options
        }
        nextButton.setVisible(false); // Hide next button
        restartButton.setVisible(true); // Show restart button

        String message = String.format("You scored %d out of %d questions!", score, questions.size());
        JOptionPane.showMessageDialog(this, message, "Quiz Result", JOptionPane.INFORMATION_MESSAGE);
    }

    private void checkAnswer() {
        int selectedOption = -1;
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isSelected()) {
                selectedOption = i;
                break;
            }
        }

        if (selectedOption == -1) {
            JOptionPane.showMessageDialog(this, "Please select an answer.", "No Answer Selected", JOptionPane.WARNING_MESSAGE);
            return; // Don't proceed to next question if no answer selected
        }

        if (selectedOption == questions.get(currentQuestionIndex).getCorrectAnswerIndex()) {
            score++; // Increment score if correct
        }

        currentQuestionIndex++; // Move to next question
        displayQuestion();      // Display next question or result
    }

    private void restartQuiz() {
        currentQuestionIndex = 0;
        score = 0;
        Collections.shuffle(questions); // Reshuffle for a new experience
        displayQuestion();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            checkAnswer();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ExamSystemApp().setVisible(true);
        });
    }
}
