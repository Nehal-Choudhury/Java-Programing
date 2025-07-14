import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Problem: Simulate a simplified ride-booking system with a GUI.
// Users can request a ride by entering pickup and drop-off locations.
// The system will display a list of available drivers (dummy data) and
// simulate a booking process, updating a log.

// Key Concepts:
// - Java Swing for GUI (JFrame, JPanel, JTextField, JButton, JLabel, JList,
//   DefaultListModel, JScrollPane, JTextArea)
// - Data Structures: ArrayList for drivers, custom Driver and Ride classes
// - Simulation of driver availability and ride assignment
// - Basic input validation and logging

public class UberLikeSystemApp extends JFrame implements ActionListener {

    private List<Driver> availableDrivers; // List of dummy drivers
    private DefaultListModel<Driver> driverListModel; // Model for JList of drivers

    // GUI Components
    private JTextField pickupLocationField;
    private JTextField dropoffLocationField;
    private JList<Driver> driverList;
    private JTextArea logArea;

    private Random random;

    // Driver Class
    private static class Driver {
        String name;
        String vehicle;
        boolean isAvailable;

        public Driver(String name, String vehicle) {
            this.name = name;
            this.vehicle = vehicle;
            this.isAvailable = true; // Initially available
        }

        public String getName() { return name; }
        public String getVehicle() { return vehicle; }
        public boolean isAvailable() { return isAvailable; }
        public void setAvailable(boolean available) { isAvailable = available; }

        @Override
        public String toString() {
            return String.format("%s (%s) - %s", name, vehicle, isAvailable ? "Available" : "Busy");
        }
    }

    // Ride Class (for logging purposes)
    private static class Ride {
        String passengerName;
        String pickup;
        String dropoff;
        Driver assignedDriver;
        String status;

        public Ride(String passengerName, String pickup, String dropoff) {
            this.passengerName = passengerName;
            this.pickup = pickup;
            this.dropoff = dropoff;
            this.status = "Pending";
        }

        public void assignDriver(Driver driver) {
            this.assignedDriver = driver;
            this.status = "Assigned to " + driver.getName();
        }

        public void completeRide() {
            this.status = "Completed";
        }

        @Override
        public String toString() {
            String driverInfo = (assignedDriver != null) ? "Driver: " + assignedDriver.getName() : "No driver assigned";
            return String.format("Ride from '%s' to '%s' | %s | Status: %s", pickup, dropoff, driverInfo, status);
        }
    }

    public UberLikeSystemApp() {
        setTitle("Simple Ride Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        random = new Random();
        initializeDrivers(); // Setup dummy drivers

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        // --- Ride Request Panel (North) ---
        JPanel requestPanel = new JPanel(new GridBagLayout());
        requestPanel.setBorder(BorderFactory.createTitledBorder("Request a Ride"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        requestPanel.add(new JLabel("Pickup Location:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        pickupLocationField = new JTextField(20);
        requestPanel.add(pickupLocationField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        requestPanel.add(new JLabel("Drop-off Location:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        dropoffLocationField = new JTextField(20);
        requestPanel.add(dropoffLocationField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton requestRideButton = new JButton("Request Ride");
        requestRideButton.setFont(new Font("Arial", Font.BOLD, 18));
        requestRideButton.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        requestRideButton.setForeground(Color.WHITE);
        requestRideButton.setFocusPainted(false);
        requestRideButton.addActionListener(this);
        requestPanel.add(requestRideButton, gbc);

        mainPanel.add(requestPanel, BorderLayout.NORTH);

        // --- Drivers & Log Panel (Center) ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // Two columns
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Driver List Panel
        JPanel driverPanel = new JPanel(new BorderLayout());
        driverPanel.setBorder(BorderFactory.createTitledBorder("Available Drivers"));
        driverListModel = new DefaultListModel<>();
        driverList = new JList<>(driverListModel);
        driverList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane driverScrollPane = new JScrollPane(driverList);
        driverPanel.add(driverScrollPane, BorderLayout.CENTER);
        centerPanel.add(driverPanel);

        // Log Area Panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("System Log"));
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        centerPanel.add(logPanel);

        updateDriverList(); // Populate initial driver list
    }

    private void initializeDrivers() {
        availableDrivers = new ArrayList<>();
        availableDrivers.add(new Driver("Alice", "Toyota Camry"));
        availableDrivers.add(new Driver("Bob", "Honda Civic"));
        availableDrivers.add(new Driver("Charlie", "Tesla Model 3"));
        availableDrivers.add(new Driver("Diana", "Ford Escape"));
        availableDrivers.add(new Driver("Eve", "Nissan Altima"));
    }

    private void updateDriverList() {
        driverListModel.clear();
        for (Driver driver : availableDrivers) {
            driverListModel.addElement(driver);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Request Ride")) {
            requestRide();
        }
    }

    private void requestRide() {
        String pickup = pickupLocationField.getText().trim();
        String dropoff = dropoffLocationField.getText().trim();

        if (pickup.isEmpty() || dropoff.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both pickup and drop-off locations.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        logArea.append("\n--- New Ride Request ---\n");
        logArea.append("Pickup: " + pickup + "\n");
        logArea.append("Drop-off: " + dropoff + "\n");

        Driver assignedDriver = findAvailableDriver();
        if (assignedDriver != null) {
            Ride newRide = new Ride("Passenger " + (random.nextInt(100) + 1), pickup, dropoff);
            newRide.assignDriver(assignedDriver);
            assignedDriver.setAvailable(false); // Mark driver as busy

            logArea.append("Status: Driver " + assignedDriver.getName() + " assigned.\n");
            logArea.append("Ride details: " + newRide.toString() + "\n");
            JOptionPane.showMessageDialog(this, "Ride requested successfully!\nDriver " + assignedDriver.getName() + " is on the way.", "Ride Booked", JOptionPane.INFORMATION_MESSAGE);

            // Simulate ride completion after a short delay
            Timer timer = new Timer(5000 + random.nextInt(5000), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    newRide.completeRide();
                    assignedDriver.setAvailable(true); // Driver becomes available again
                    logArea.append("Ride completed: " + newRide.toString() + "\n");
                    updateDriverList(); // Refresh driver list
                    ((Timer)e.getSource()).stop(); // Stop the timer
                }
            });
            timer.setRepeats(false); // Only run once
            timer.start();

        } else {
            logArea.append("Status: No available drivers at the moment.\n");
            JOptionPane.showMessageDialog(this, "No drivers available. Please try again later.", "No Drivers", JOptionPane.INFORMATION_MESSAGE);
        }
        logArea.append("--------------------------\n");
        updateDriverList(); // Refresh driver list immediately after trying to assign
        pickupLocationField.setText("");
        dropoffLocationField.setText("");
    }

    private Driver findAvailableDriver() {
        List<Driver> currentlyAvailable = new ArrayList<>();
        for (Driver driver : availableDrivers) {
            if (driver.isAvailable()) {
                currentlyAvailable.add(driver);
            }
        }

        if (currentlyAvailable.isEmpty()) {
            return null;
        }
        // Return a random available driver
        return currentlyAvailable.get(random.nextInt(currentlyAvailable.size()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UberLikeSystemApp().setVisible(true);
        });
    }
}
