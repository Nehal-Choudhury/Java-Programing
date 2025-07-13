import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// Problem: Develop a To-Do List application with a GUI.
// Users should be able to:
// 1. Add new tasks.
// 2. Mark tasks as complete/incomplete.
// 3. Delete tasks.
// 4. Display all tasks.

// Key Concepts:
// - Java Swing for GUI (JFrame, JPanel, JTextField, JButton, JList, DefaultListModel, JScrollPane)
// - Data Structure: ArrayList to store tasks
// - Custom List Cell Renderer for displaying tasks with checkboxes
// - Event handling for buttons and list selections

public class TodoListApp extends JFrame {

    private DefaultListModel<Task> listModel; // Model for JList to hold Task objects
    private JList<Task> taskList;            // JList to display tasks
    private JTextField taskInputField;       // Input field for new tasks

    // Task class to hold task details (description and completion status)
    private static class Task {
        String description;
        boolean isCompleted;

        public Task(String description) {
            this.description = description;
            this.isCompleted = false; // New tasks are incomplete by default
        }

        public String getDescription() {
            return description;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        public void setCompleted(boolean completed) {
            isCompleted = completed;
        }

        @Override
        public String toString() {
            // This is used by the default list model, but we'll use a custom renderer
            return description;
        }
    }

    // Custom ListCellRenderer to display tasks with a checkbox
    private class TaskListCellRenderer extends JCheckBox implements ListCellRenderer<Task> {
        public TaskListCellRenderer() {
            setOpaque(true); // Necessary for background painting
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Task> list, Task value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText(value.getDescription());
            setSelected(value.isCompleted());

            // Set colors based on selection and completion status
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            // Optional: Strikethrough for completed tasks (visual only)
            if (value.isCompleted()) {
                Font font = getFont();
                setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
                setForeground(Color.GRAY); // Dim completed tasks
            } else {
                setFont(list.getFont());
                setForeground(list.getForeground());
            }

            return this;
        }
    }

    public TodoListApp() {
        setTitle("To-Do List Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null); // Center the window

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main Panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Input Panel (North)
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        taskInputField = new JTextField();
        taskInputField.setFont(new Font("Arial", Font.PLAIN, 18));
        taskInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        inputPanel.add(taskInputField, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Task");
        addButton.setFont(new Font("Arial", Font.BOLD, 16));
        addButton.setBackground(new Color(70, 130, 180)); // SteelBlue
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        inputPanel.add(addButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Task List Panel (Center)
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 18));
        taskList.setCellRenderer(new TaskListCellRenderer()); // Set custom renderer
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one task can be selected

        // Add mouse listener to toggle task completion
        taskList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) { // Single click
                    int index = taskList.locationToIndex(evt.getPoint());
                    if (index != -1) {
                        Task task = listModel.getElementAt(index);
                        task.setCompleted(!task.isCompleted()); // Toggle completion status
                        taskList.repaint(); // Repaint the list to show changes
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel (South)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton deleteButton = new JButton("Delete Task");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteButton.setBackground(new Color(220, 20, 60)); // Crimson
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        taskInputField.addActionListener(new ActionListener() { // Add on Enter key press
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTask();
            }
        });
    }

    private void addTask() {
        String taskDescription = taskInputField.getText().trim();
        if (!taskDescription.isEmpty()) {
            listModel.addElement(new Task(taskDescription));
            taskInputField.setText(""); // Clear input field
        } else {
            JOptionPane.showMessageDialog(this, "Task description cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TodoListApp().setVisible(true);
        });
    }
}
