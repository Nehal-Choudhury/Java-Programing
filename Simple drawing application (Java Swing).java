import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

// Problem: Create a simple drawing application where users can draw lines
// and rectangles on a canvas.
// The application should allow:
// 1. Drawing freehand lines by dragging the mouse.
// 2. Drawing rectangles by dragging the mouse (from top-left to bottom-right).
// 3. Clearing the canvas.
// 4. Selecting drawing color.

// Key Concepts:
// - Java Swing for GUI (JFrame, JPanel, JButton, JColorChooser)
// - Custom JPanel for drawing (overriding paintComponent)
// - MouseListener and MouseMotionListener for drawing events
// - Data Structures: ArrayList to store drawn shapes (lines, rectangles)
// - Polymorphism: Using a common interface/abstract class for shapes

public class SimpleDrawingApp extends JFrame {

    private DrawingPanel drawingPanel;
    private Color currentColor = Color.BLACK; // Default drawing color
    private String currentTool = "Freehand"; // "Freehand" or "Rectangle"

    // Abstract base class for all drawable shapes
    private abstract class Shape {
        protected Color color;

        public Shape(Color color) {
            this.color = color;
        }

        public abstract void draw(Graphics g);
    }

    // Concrete class for a Line
    private class Line extends Shape {
        int x1, y1, x2, y2;

        public Line(int x1, int y1, int x2, int y2, Color color) {
            super(color);
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public void draw(Graphics g) {
            g.setColor(color);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    // Concrete class for a Rectangle
    private class Rectangle extends Shape {
        int x, y, width, height;

        public Rectangle(int x, int y, int width, int height, Color color) {
            super(color);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public void draw(Graphics g) {
            g.setColor(color);
            g.drawRect(x, y, width, height);
        }
    }

    // Custom JPanel for drawing
    private class DrawingPanel extends JPanel {
        private List<Shape> shapes; // List to store all drawn shapes
        private Point startPoint;   // Start point for drawing (for current shape)
        private Shape currentDrawingShape; // The shape currently being drawn

        public DrawingPanel() {
            shapes = new ArrayList<>();
            setBackground(Color.WHITE); // White canvas background
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

            // Mouse Listener for press and release
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint(); // Store starting point
                    if (currentTool.equals("Freehand")) {
                        // For freehand, start drawing a tiny line
                        currentDrawingShape = new Line(startPoint.x, startPoint.y, startPoint.x, startPoint.y, currentColor);
                        shapes.add(currentDrawingShape); // Add to shapes list immediately
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (currentTool.equals("Rectangle")) {
                        // For rectangle, finalize the shape and add it
                        if (startPoint != null) {
                            int x = Math.min(startPoint.x, e.getX());
                            int y = Math.min(startPoint.y, e.getY());
                            int width = Math.abs(e.getX() - startPoint.x);
                            int height = Math.abs(e.getY() - startPoint.y);
                            shapes.add(new Rectangle(x, y, width, height, currentColor));
                        }
                    }
                    startPoint = null; // Reset start point
                    currentDrawingShape = null; // Reset current drawing shape
                    repaint(); // Redraw the panel
                }
            });

            // Mouse Motion Listener for dragging
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (startPoint == null) return; // Should not happen if mousePressed was handled

                    if (currentTool.equals("Freehand")) {
                        // For freehand, continuously add small lines
                        // This creates the effect of continuous drawing
                        shapes.add(new Line(
                            ((Line) currentDrawingShape).x2, ((Line) currentDrawingShape).y2,
                            e.getX(), e.getY(), currentColor));
                        ((Line) currentDrawingShape).x2 = e.getX(); // Update end point for next segment
                        ((Line) currentDrawingShape).y2 = e.getY();
                    } else if (currentTool.equals("Rectangle")) {
                        // For rectangle, update the temporary drawing shape
                        int x = Math.min(startPoint.x, e.getX());
                        int y = Math.min(startPoint.y, e.getY());
                        int width = Math.abs(e.getX() - startPoint.x);
                        int height = Math.abs(e.getY() - startPoint.y);
                        currentDrawingShape = new Rectangle(x, y, width, height, currentColor);
                    }
                    repaint(); // Redraw the panel to show the current drawing progress
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Call JPanel's paintComponent to clear background

            // Draw all previously stored shapes
            for (Shape shape : shapes) {
                shape.draw(g);
            }

            // Draw the current shape being dragged (if any)
            if (currentDrawingShape != null) {
                currentDrawingShape.draw(g);
            }
        }

        // Clear all drawn shapes
        public void clearDrawing() {
            shapes.clear();
            repaint(); // Redraw to show empty canvas
        }
    }

    public SimpleDrawingApp() {
        setTitle("Simple Drawing Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        add(contentPanel);

        // Drawing Panel (Center)
        drawingPanel = new DrawingPanel();
        contentPanel.add(drawingPanel, BorderLayout.CENTER);

        // Control Panel (North)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(new Color(230, 230, 230)); // Light gray background
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Tool Selection
        ButtonGroup toolGroup = new ButtonGroup();
        JRadioButton freehandButton = new JRadioButton("Freehand");
        JRadioButton rectangleButton = new JRadioButton("Rectangle");

        freehandButton.setSelected(true); // Freehand is default
        freehandButton.addActionListener(e -> currentTool = "Freehand");
        rectangleButton.addActionListener(e -> currentTool = "Rectangle");

        toolGroup.add(freehandButton);
        toolGroup.add(rectangleButton);

        controlPanel.add(new JLabel("Tool:"));
        controlPanel.add(freehandButton);
        controlPanel.add(rectangleButton);

        // Color Chooser Button
        JButton colorButton = new JButton("Choose Color");
        colorButton.setBackground(new Color(100, 149, 237)); // CornflowerBlue
        colorButton.setForeground(Color.WHITE);
        colorButton.setFocusPainted(false);
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(this, "Choose Drawing Color", currentColor);
            if (selectedColor != null) {
                currentColor = selectedColor;
            }
        });
        controlPanel.add(colorButton);

        // Clear Button
        JButton clearButton = new JButton("Clear Canvas");
        clearButton.setBackground(new Color(220, 20, 60)); // Crimson
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> drawingPanel.clearDrawing());
        controlPanel.add(clearButton);

        contentPanel.add(controlPanel, BorderLayout.NORTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleDrawingApp().setVisible(true);
        });
    }
}
