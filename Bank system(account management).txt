import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// Problem: Develop a basic bank account management system with a GUI.
// Users can:
// 1. Create new accounts (with a generated account number).
// 2. Deposit money into an existing account.
// 3. Withdraw money from an existing account.
// 4. View an account's balance.
// 5. Display a transaction log for an account.

// Key Concepts:
// - Java Swing for GUI (JFrame, JPanel, JTextField, JButton, JLabel, JTextArea, JScrollPane)
// - Data Structure: HashMap to store Account objects (account number as key)
// - Custom Account class to encapsulate account details and methods
// - Basic transaction logic with error handling (insufficient funds, invalid input)
// - Random number generation for account numbers

public class BankSystemApp extends JFrame implements ActionListener {

    private Map<String, Account> accounts; // Stores accounts: Account Number -> Account Object
    private Random random; // For generating account numbers
    private DecimalFormat currencyFormat; // For formatting currency

    // GUI Components
    private JTextField accountNumberField;
    private JTextField amountField;
    private JTextArea transactionLogArea;

    // Account Class
    private static class Account {
        String accountNumber;
        double balance;
        List<String> transactions; // Simple log of transactions

        public Account(String accountNumber) {
            this.accountNumber = accountNumber;
            this.balance = 0.0;
            this.transactions = new ArrayList<>();
            this.transactions.add("Account created with initial balance: $0.00");
        }

        public String getAccountNumber() { return accountNumber; }
        public double getBalance() { return balance; }

        public void deposit(double amount) {
            balance += amount;
            transactions.add(String.format("Deposited: $%.2f. New balance: $%.2f", amount, balance));
        }

        public boolean withdraw(double amount) {
            if (balance >= amount) {
                balance -= amount;
                transactions.add(String.format("Withdrew: $%.2f. New balance: $%.2f", amount, balance));
                return true;
            }
            transactions.add(String.format("Withdrawal failed: Insufficient funds for $%.2f", amount));
            return false;
        }

        public List<String> getTransactions() { return transactions; }
    }

    public BankSystemApp() {
        setTitle("Simple Bank System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        accounts = new HashMap<>();
        random = new Random();
        currencyFormat = new DecimalFormat("$#,##0.00");

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        // --- Input and Action Panel (North) ---
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBorder(BorderFactory.createTitledBorder("Account Operations"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Account Number
        gbc.gridx = 0; gbc.gridy = 0;
        actionPanel.add(new JLabel("Account No:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        accountNumberField = new JTextField(15);
        actionPanel.add(accountNumberField, gbc);
        gbc.weightx = 0; // Reset weight

        // Row 1: Amount
        gbc.gridx = 0; gbc.gridy = 1;
        actionPanel.add(new JLabel("Amount ($):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        amountField = new JTextField(15);
        actionPanel.add(amountField, gbc);
        gbc.weightx = 0;

        // Row 2: Buttons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Buttons should not fill horizontally
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton createButton = new JButton("Create Account");
        createButton.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.addActionListener(this);
        buttonPanel.add(createButton);

        JButton depositButton = new JButton("Deposit");
        depositButton.setBackground(new Color(70, 130, 180)); // SteelBlue
        depositButton.setForeground(Color.WHITE);
        depositButton.setFocusPainted(false);
        depositButton.addActionListener(this);
        buttonPanel.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setBackground(new Color(255, 99, 71)); // Tomato
        withdrawButton.setForeground(Color.WHITE);
        withdrawButton.setFocusPainted(false);
        withdrawButton.addActionListener(this);
        buttonPanel.add(withdrawButton);

        JButton viewBalanceButton = new JButton("View Balance");
        viewBalanceButton.setBackground(new Color(255, 165, 0)); // Orange
        viewBalanceButton.setForeground(Color.WHITE);
        viewBalanceButton.setFocusPainted(false);
        viewBalanceButton.addActionListener(this);
        buttonPanel.add(viewBalanceButton);

        actionPanel.add(buttonPanel, gbc);
        mainPanel.add(actionPanel, BorderLayout.NORTH);

        // --- Transaction Log Area (Center) ---
        transactionLogArea = new JTextArea();
        transactionLogArea.setEditable(false);
        transactionLogArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        transactionLogArea.setBorder(BorderFactory.createTitledBorder("Transaction Log"));
        JScrollPane scrollPane = new JScrollPane(transactionLogArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Create Account":
                createAccount();
                break;
            case "Deposit":
                performTransaction("deposit");
                break;
            case "Withdraw":
                performTransaction("withdraw");
                break;
            case "View Balance":
                viewBalance();
                break;
        }
    }

    private String generateAccountNumber() {
        // Generate a simple 8-digit account number
        return String.format("%08d", random.nextInt(100000000));
    }

    private void createAccount() {
        String newAccNum;
        do {
            newAccNum = generateAccountNumber();
        } while (accounts.containsKey(newAccNum)); // Ensure unique account number

        Account newAccount = new Account(newAccNum);
        accounts.put(newAccNum, newAccount);
        transactionLogArea.append("--- New Account Created ---\n");
        transactionLogArea.append("Account Number: " + newAccNum + "\n");
        transactionLogArea.append("Initial Balance: " + currencyFormat.format(newAccount.getBalance()) + "\n");
        transactionLogArea.append("---------------------------\n");
        JOptionPane.showMessageDialog(this, "Account created successfully!\nAccount Number: " + newAccNum, "Account Created", JOptionPane.INFORMATION_MESSAGE);
        accountNumberField.setText(newAccNum); // Pre-fill account number for convenience
    }

    private void performTransaction(String type) {
        String accNum = accountNumberField.getText().trim();
        if (accNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an account number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Account account = accounts.get(accNum);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            transactionLogArea.append("\n--- Account: " + accNum + " ---\n");
            if (type.equals("deposit")) {
                account.deposit(amount);
                transactionLogArea.append(String.format("Deposited %s. New Balance: %s\n", currencyFormat.format(amount), currencyFormat.format(account.getBalance())));
            } else if (type.equals("withdraw")) {
                if (account.withdraw(amount)) {
                    transactionLogArea.append(String.format("Withdrew %s. New Balance: %s\n", currencyFormat.format(amount), currencyFormat.format(account.getBalance())));
                } else {
                    transactionLogArea.append(String.format("Withdrawal of %s failed: Insufficient funds. Current Balance: %s\n", currencyFormat.format(amount), currencyFormat.format(account.getBalance())));
                    JOptionPane.showMessageDialog(this, "Insufficient funds for withdrawal.", "Transaction Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
            transactionLogArea.append("---------------------------\n");
            amountField.setText(""); // Clear amount field
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewBalance() {
        String accNum = accountNumberField.getText().trim();
        if (accNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an account number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Account account = accounts.get(accNum);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        transactionLogArea.append("\n--- Account Balance & History for " + accNum + " ---\n");
        transactionLogArea.append("Current Balance: " + currencyFormat.format(account.getBalance()) + "\n");
        transactionLogArea.append("Transaction History:\n");
        if (account.getTransactions().isEmpty()) {
            transactionLogArea.append("  No transactions yet.\n");
        } else {
            for (String log : account.getTransactions()) {
                transactionLogArea.append("  - " + log + "\n");
            }
        }
        transactionLogArea.append("-------------------------------------------\n");
        JOptionPane.showMessageDialog(this, "Account " + accNum + " Balance: " + currencyFormat.format(account.getBalance()), "Account Balance", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BankSystemApp().setVisible(true);
        });
    }
}
