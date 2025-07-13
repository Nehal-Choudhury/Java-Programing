import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Problem: Develop a simple marriage registration and matching system.
// Users can register with basic details (name, age, gender, preferences).
// The system can then display registered users and perform a very basic "match"
// based on gender and age range.

// Key Concepts:
// - Java Swing for GUI (JFrame, JPanel, JTextField, JTextArea, JButton,
//   JRadioButton, ButtonGroup, JComboBox, JScrollPane, JLabel)
// - Data Structure: ArrayList to store User profiles
// - Custom User class to encapsulate profile data
// - Simple matching logic based on specified criteria

public class MarriageSystemApp extends JFrame {

    private List<User> registeredUsers; // Stores all registered users

    // GUI Components for Registration
    private JTextField nameField;
    private JTextField ageField;
    private ButtonGroup genderGroup;
    private JRadioButton maleRadio, femaleRadio;
    private JComboBox<String> preferredGenderCombo;
    private JTextField preferredMinAgeField;
    private JTextField preferredMaxAgeField;

    // GUI Components for Display/Matching
    private JTextArea displayArea;

    // User Class to hold profile data
    private static class User {
        String name;
        int age;
        String gender; // "Male" or "Female"
        String preferredGender; // "Male", "Female", or "Any"
        int preferredMinAge;
        int preferredMaxAge;

        public User(String name, int age, String gender, String preferredGender, int preferredMinAge, int preferredMaxAge) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.preferredGender = preferredGender;
            this.preferredMinAge = preferredMinAge;
            this.preferredMaxAge = preferredMaxAge;
        }

        @Override
        public String toString() {
            return String.format("Name: %s, Age: %d, Gender: %s, Prefers: %s (Age %d-%d)",
                    name, age, gender, preferredGender, preferredMinAge, preferredMaxAge);
        }

        // Basic matching logic
        public boolean matches(User potentialMatch) {
            // 1. Genders must be opposite (or one prefers "Any")
            boolean genderMatch = false;
            if (this.preferredGender.equals("Any") || potentialMatch.preferredGender.equals("Any")) {
                genderMatch = true; // If either prefers "Any", gender is not a strict filter
            } else if (!this.gender.equals(potentialMatch.gender) && // Genders must be different
                       this.preferredGender.equals(potentialMatch.gender) && // This user prefers potentialMatch's gender
                       potentialMatch.preferredGender.equals(this.gender)) { // Potential match prefers this user's gender
                genderMatch = true;
            }

            if (!genderMatch) return false;

            // 2. Age range compatibility (mutual preference)
            boolean ageMatch = (potentialMatch.age >= this.preferredMinAge && potentialMatch.age <= this.preferredMaxAge) &&
                               (this.age >= potentialMatch.preferredMinAge && this.age <= potentialMatch.preferredMaxAge);

            return genderMatch && ageMatch;
        }
    }

    public MarriageSystemApp() {
        setTitle("Marriage System: Registration & Matching");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        registeredUsers = new ArrayList<>();

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // --- Registration Panel (North) ---
        JPanel registrationPanel = new JPanel(new GridBagLayout());
        registrationPanel.setBorder(BorderFactory.createTitledBorder("Register New User"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Name
        gbc.gridx = 0; gbc.gridy = 0;
        registrationPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3;
        nameField = new JTextField(20);
        registrationPanel.add(nameField, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Row 1: Age
        gbc.gridx = 0; gbc.gridy = 1;
        registrationPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        ageField = new JTextField(5);
        registrationPanel.add(ageField, gbc);

        // Row 2: Gender
        gbc.gridx = 0; gbc.gridy = 2;
        registrationPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        registrationPanel.add(genderPanel, gbc);
        gbc.gridwidth = 1;

        // Row 3: Preferred Gender
        gbc.gridx = 0; gbc.gridy = 3;
        registrationPanel.add(new JLabel("Pref. Gender:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        preferredGenderCombo = new JComboBox<>(new String[]{"Any", "Male", "Female"});
        registrationPanel.add(preferredGenderCombo, gbc);
        gbc.gridwidth = 1;

        // Row 4: Preferred Age Range
        gbc.gridx = 0; gbc.gridy = 4;
        registrationPanel.add(new JLabel("Pref. Age Range:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        preferredMinAgeField = new JTextField(5);
        registrationPanel.add(preferredMinAgeField, gbc);
        gbc.gridx = 2; gbc.gridy = 4;
        registrationPanel.add(new JLabel("to"), gbc);
        gbc.gridx = 3; gbc.gridy = 4;
        preferredMaxAgeField = new JTextField(5);
        registrationPanel.add(preferredMaxAgeField, gbc);

        // Row 5: Register Button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerButton = new JButton("Register User");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> registerUser());
        registrationPanel.add(registerButton, gbc);

        mainPanel.add(registrationPanel, BorderLayout.NORTH);

        // --- Display Area (Center) ---
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setBorder(BorderFactory.createTitledBorder("Registered Users & Matches"));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Control Panel (South) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton showAllButton = new JButton("Show All Users");
        showAllButton.setFont(new Font("Arial", Font.BOLD, 14));
        showAllButton.setBackground(new Color(70, 130, 180)); // SteelBlue
        showAllButton.setForeground(Color.WHITE);
        showAllButton.setFocusPainted(false);
        showAllButton.addActionListener(e -> displayAllUsers());
        controlPanel.add(showAllButton);

        JButton findMatchesButton = new JButton("Find Matches for Selected User");
        findMatchesButton.setFont(new Font("Arial", Font.BOLD, 14));
        findMatchesButton.setBackground(new Color(255, 165, 0)); // Orange
        findMatchesButton.setForeground(Color.WHITE);
        findMatchesButton.setFocusPainted(false);
        findMatchesButton.addActionListener(e -> findMatches());
        controlPanel.add(findMatchesButton);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);
    }

    private void registerUser() {
        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String gender = maleRadio.isSelected() ? "Male" : (femaleRadio.isSelected() ? "Female" : null);
            String preferredGender = (String) preferredGenderCombo.getSelectedItem();
            int preferredMinAge = Integer.parseInt(preferredMinAgeField.getText().trim());
            int preferredMaxAge = Integer.parseInt(preferredMaxAgeField.getText().trim());

            if (name.isEmpty() || gender == null || age <= 0 || preferredMinAge < 0 || preferredMaxAge < preferredMinAge) {
                JOptionPane.showMessageDialog(this, "Please fill all fields correctly.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new User(name, age, gender, preferredGender, preferredMinAge, preferredMaxAge);
            registeredUsers.add(newUser);
            JOptionPane.showMessageDialog(this, "User '" + name + "' registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear registration fields
            nameField.setText("");
            ageField.setText("");
            genderGroup.clearSelection();
            preferredGenderCombo.setSelectedIndex(0);
            preferredMinAgeField.setText("");
            preferredMaxAgeField.setText("");

            displayAllUsers(); // Refresh display
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for age fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayAllUsers() {
        displayArea.setText("--- All Registered Users ---\n");
        if (registeredUsers.isEmpty()) {
            displayArea.append("No users registered yet.\n");
            return;
        }
        for (int i = 0; i < registeredUsers.size(); i++) {
            displayArea.append(String.format("%d. %s\n", i + 1, registeredUsers.get(i).toString()));
        }
    }

    private void findMatches() {
        if (registeredUsers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No users registered to find matches.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter the number of the user to find matches for:");
        if (input == null || input.trim().isEmpty()) {
            return; // User cancelled
        }

        try {
            int userIndex = Integer.parseInt(input) - 1; // Convert to 0-based index
            if (userIndex < 0 || userIndex >= registeredUsers.size()) {
                JOptionPane.showMessageDialog(this, "Invalid user number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User targetUser = registeredUsers.get(userIndex);
            List<User> potentialMatches = new ArrayList<>();

            for (User otherUser : registeredUsers) {
                if (otherUser != targetUser && targetUser.matches(otherUser)) {
                    potentialMatches.add(otherUser);
                }
            }

            displayArea.setText(String.format("--- Matches for %s (%s, %d) ---\n",
                    targetUser.name, targetUser.gender, targetUser.age));

            if (potentialMatches.isEmpty()) {
                displayArea.append("No matches found for " + targetUser.name + " based on current criteria.\n");
            } else {
                for (int i = 0; i < potentialMatches.size(); i++) {
                    displayArea.append(String.format("%d. %s\n", i + 1, potentialMatches.get(i).toString()));
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MarriageSystemApp().setVisible(true);
        });
    }
}
