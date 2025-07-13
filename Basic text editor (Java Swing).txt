import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

// Problem: Create a basic text editor with a GUI that allows users to:
// 1. Open a text file.
// 2. Save the current text to a file.
// 3. Edit text in a JTextArea.
// 4. Implement basic menu bar functionality (File -> New, Open, Save, Exit).

// Key Concepts:
// - Java Swing for GUI (JFrame, JTextArea, JScrollPane, JMenuBar, JMenu, JMenuItem, JFileChooser)
// - File I/O operations (FileReader, FileWriter, BufferedReader, BufferedWriter)
// - Event handling for menu items and file choosers

public class SimpleTextEditor extends JFrame implements ActionListener {

    private JTextArea textArea; // Text area for editing
    private JFileChooser fileChooser; // File dialog for open/save operations
    private File currentFile; // Stores the currently open file

    public SimpleTextEditor() {
        setTitle("Simple Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Text Area
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 16)); // Monospaced font for code/text
        textArea.setLineWrap(true); // Wrap lines
        textArea.setWrapStyleWord(true); // Wrap at word boundaries
        JScrollPane scrollPane = new JScrollPane(textArea); // Add scrollability
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // File Chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open...");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        newMenuItem.addActionListener(this);
        openMenuItem.addActionListener(this);
        saveMenuItem.addActionListener(this);
        saveAsMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator(); // Separator line
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar); // Set the menu bar for the frame

        // Initialize with no current file
        currentFile = null;
        updateTitle();
    }

    // Update the frame title to show the current file name
    private void updateTitle() {
        String fileName = (currentFile == null) ? "Untitled" : currentFile.getName();
        setTitle("Simple Text Editor - " + fileName);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "New":
                newFile();
                break;
            case "Open...":
                openFile();
                break;
            case "Save":
                saveFile();
                break;
            case "Save As...":
                saveFileAs();
                break;
            case "Exit":
                exitApplication();
                break;
        }
    }

    private void newFile() {
        // Ask to save current file if modified
        if (textArea.getText().length() > 0 && currentFile != null) {
            int option = JOptionPane.showConfirmDialog(this, "Do you want to save changes to " + currentFile.getName() + "?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                if (!saveFile()) { // If save fails or is cancelled, don't create new file
                    return;
                }
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return; // User cancelled new file operation
            }
        }
        textArea.setText("");
        currentFile = null;
        updateTitle();
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                textArea.read(reader, null); // Read content into textarea
                currentFile = selectedFile;
                updateTitle();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean saveFile() {
        if (currentFile == null) {
            return saveFileAs(); // If no file is open, treat as Save As
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                textArea.write(writer); // Write content from textarea to file
                JOptionPane.showMessageDialog(this, "File saved successfully!", "Save", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    private boolean saveFileAs() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Ensure .txt extension if not provided
            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                textArea.write(writer);
                currentFile = selectedFile;
                updateTitle();
                JOptionPane.showMessageDialog(this, "File saved successfully!", "Save As", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false; // User cancelled Save As
    }

    private void exitApplication() {
        // Ask to save before exiting if content is not empty
        if (textArea.getText().length() > 0 && currentFile == null) {
             int option = JOptionPane.showConfirmDialog(this, "Do you want to save your untitled document?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
             if (option == JOptionPane.YES_OPTION) {
                 if (!saveFileAs()) { // If save fails or is cancelled, don't exit
                     return;
                 }
             } else if (option == JOptionPane.CANCEL_OPTION) {
                 return; // User cancelled exit
             }
        } else if (textArea.getText().length() > 0 && currentFile != null) {
            // More robust check would compare current text with saved text
            // For simplicity, assume if text area has content and file is open, ask to save.
            int option = JOptionPane.showConfirmDialog(this, "Do you want to save changes to " + currentFile.getName() + "?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                if (!saveFile()) {
                    return;
                }
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleTextEditor().setVisible(true);
        });
    }
}
