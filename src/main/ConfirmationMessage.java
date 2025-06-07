package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmationMessage extends JWindow {
    private JLabel messageLabel;
    private Timer timer;
    private static ConfirmationMessage currentMessage = null;

    public ConfirmationMessage(String message) {
        // Create a transparent window
        setBackground(new Color(0, 0, 0, 0));
        
        // Create the message label
        messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 24));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Create a semi-transparent background panel
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 128)); // Fixed 50% transparency
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.add(messageLabel, BorderLayout.CENTER);
        
        // Add padding
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Add the panel to the window
        setContentPane(panel);
        
        // Set up the timer for auto-close
        timer = new Timer(1000, e -> {
            dispose();
            timer.stop();
            if (currentMessage == this) {
                currentMessage = null;
            }
        });
    }

    public void showMessage(Component parent, int xOffset, int yOffset, int gameAreaWidth, int gameAreaHeight) {
        // If there's a current message, dispose it
        if (currentMessage != null) {
            currentMessage.dispose();
            currentMessage.timer.stop();
        }
        
        // Set this as the current message
        currentMessage = this;
        
        // Pack and show the window
        pack();
        // Center in the game area
        int x = parent.getX() + xOffset + (gameAreaWidth - getWidth()) / 2;
        int y = parent.getY() + yOffset + (gameAreaHeight - getHeight()) / 2;
        setLocation(x, y);
        setVisible(true);
        
        // Start the timer
        timer.start();
    }

    // Backward compatibility: old showMessage signature
    public void showMessage(Component parent) {
        // Default to centering in the whole window
        showMessage(parent, 0, 0, parent.getWidth(), parent.getHeight());
    }
} 