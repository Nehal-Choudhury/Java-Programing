import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Problem: Create a simple calculator with a graphical user interface (GUI)
// that can perform basic arithmetic operations: addition, subtraction,
// multiplication, and division.

// Key Concepts:
// - Java Swing for GUI components (JFrame, JPanel, JButton, JTextField)
// - ActionListener for event handling
// - Basic arithmetic operations
// - Error handling for division by zero and invalid input

public class SimpleCalculator extends JFrame implements ActionListener {

    private JTextField displayField; // Text field to display input and results
    private String currentInput = ""; // Stores the current number being entered
    private String operator = "";     // Stores the selected operator (+, -, *, /)
    private double firstOperand = 0;  // Stores the first number for calculation
    private boolean newNumber = true; // Flag to indicate if a new number is being entered

    public SimpleCalculator() {
        setTitle("Simple Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null); // Center the window

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Display Field
        displayField = new JTextField("0");
        displayField.setEditable(false); // User cannot type directly
        displayField.setHorizontalAlignment(JTextField.RIGHT); // Align text to the right
        displayField.setFont(new Font("Arial", Font.BOLD, 30));
        displayField.setBackground(new Color(240, 240, 240)); // Light gray background
        displayField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        add(displayField, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 10, 10)); // 5 rows, 4 columns, with gaps
        buttonPanel.setBackground(new Color(60, 60, 60)); // Dark background for buttons

        String[] buttonLabels = {
                "C", "+/-", "%", "/",
                "7", "8", "9", "*",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "="
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.PLAIN, 24));
            button.addActionListener(this);

            // Customize button appearance
            if (label.matches("[0-9.]")) { // Number and decimal buttons
                button.setBackground(new Color(90, 90, 90)); // Darker gray
                button.setForeground(Color.WHITE);
            } else if (label.matches("[+\\-*/=]")) { // Operator and equals buttons
                button.setBackground(new Color(255, 165, 0)); // Orange
                button.setForeground(Color.WHITE);
            } else { // Clear, +/- , % buttons
                button.setBackground(new Color(120, 120, 120)); // Lighter gray
                button.setForeground(Color.WHITE);
            }
            button.setFocusPainted(false); // Remove focus border
            button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding
            button.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1)); // Subtle border
            button.setOpaque(true); // Ensure background is painted
            buttonPanel.add(button);
        }

        // Add a dummy button for the last row to make '0' span two columns
        // This is a common trick for calculator layouts
        JButton dummyButton = new JButton();
        dummyButton.setVisible(false); // Make it invisible
        buttonPanel.add(dummyButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.matches("[0-9]")) { // Number buttons
            if (newNumber) {
                currentInput = command;
                newNumber = false;
            } else {
                currentInput += command;
            }
            displayField.setText(currentInput);
        } else if (command.equals(".")) { // Decimal button
            if (newNumber) {
                currentInput = "0.";
                newNumber = false;
            } else if (!currentInput.contains(".")) {
                currentInput += ".";
            }
            displayField.setText(currentInput);
        } else if (command.equals("C")) { // Clear button
            currentInput = "";
            operator = "";
            firstOperand = 0;
            newNumber = true;
            displayField.setText("0");
        } else if (command.equals("+/-")) { // Negate button
            if (!currentInput.isEmpty() && !currentInput.equals("0")) {
                if (currentInput.startsWith("-")) {
                    currentInput = currentInput.substring(1);
                } else {
                    currentInput = "-" + currentInput;
                }
                displayField.setText(currentInput);
            }
        } else if (command.equals("%")) { // Percentage button (simple division by 100)
            if (!currentInput.isEmpty()) {
                try {
                    double val = Double.parseDouble(currentInput);
                    currentInput = String.valueOf(val / 100.0);
                    displayField.setText(currentInput);
                } catch (NumberFormatException ex) {
                    displayField.setText("Error");
                    currentInput = "";
                    newNumber = true;
                }
            }
        } else if (command.matches("[+\\-*/]")) { // Operator buttons
            if (!currentInput.isEmpty()) {
                try {
                    firstOperand = Double.parseDouble(currentInput);
                    operator = command;
                    newNumber = true; // Ready for the next number
                } catch (NumberFormatException ex) {
                    displayField.setText("Error");
                    currentInput = "";
                    newNumber = true;
                }
            }
        } else if (command.equals("=")) { // Equals button
            if (!currentInput.isEmpty() && !operator.isEmpty()) {
                try {
                    double secondOperand = Double.parseDouble(currentInput);
                    double result = 0;

                    switch (operator) {
                        case "+":
                            result = firstOperand + secondOperand;
                            break;
                        case "-":
                            result = firstOperand - secondOperand;
                            break;
                        case "*":
                            result = firstOperand * secondOperand;
                            break;
                        case "/":
                            if (secondOperand != 0) {
                                result = firstOperand / secondOperand;
                            } else {
                                displayField.setText("Error: Div by 0");
                                currentInput = "";
                                operator = "";
                                newNumber = true;
                                return;
                            }
                            break;
                    }
                    currentInput = String.valueOf(result);
                    displayField.setText(currentInput);
                    operator = ""; // Reset operator
                    newNumber = true; // Result becomes the new first operand for chained operations
                    firstOperand = result; // Store result for potential next operation
                } catch (NumberFormatException ex) {
                    displayField.setText("Error");
                    currentInput = "";
                    operator = "";
                    newNumber = true;
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleCalculator().setVisible(true);
        });
    }
}
