/////////////////////////////////////////////////////////////////////////////
// Limitless
// ConfirmationMessage.java
// 
// Description: Handles confirmation dialogs including:
// - Save confirmation
// - Load confirmation
// - Quit confirmation
// - Overwrite confirmation
/////////////////////////////////////////////////////////////////////////////

package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmationMessage extends JWindow {
    private JLabel messageLabel;
    private Timer fadeTimer;
    private float opacity = 0f;
    private static ConfirmationMessage currentMessage = null;
    private Icon icon;
    private boolean fadeIn = true;
    private static final int FADE_DURATION = 300; // ms
    private static final int SHOW_DURATION = 1200; // ms
    private static final int FADE_INTERVAL = 30; // ms

    // Dialog settings
    private static final int WIDTH = 400;              // Dialog width
    private static final int HEIGHT = 200;             // Dialog height
    private static final int PADDING = 20;             // Dialog padding
    
    // Button settings
    private static final int BUTTON_WIDTH = 100;       // Button width
    private static final int BUTTON_HEIGHT = 30;       // Button height
    private static final int BUTTON_SPACING = 20;      // Space between buttons
    
    // Text settings
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);    // Title font
    private static final Font MESSAGE_FONT = new Font("Arial", Font.PLAIN, 14); // Message font

    public ConfirmationMessage(String message) {
        this(message, null);
    }

    public ConfirmationMessage(String message, Icon icon) {
        setBackground(new Color(0, 0, 0, 0));
        this.icon = icon;

        // Message label
        messageLabel = new JLabel(message, icon, SwingConstants.LEFT);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setIconTextGap(18);

        // Panel with drop shadow and rounded corners
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shadow
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 32, 32);
                // Main background
                g2d.setColor(new Color(30, 30, 30, 220));
                g2d.fillRoundRect(0, 0, getWidth() - 0, getHeight() - 0, 32, 32);
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 48, 28, 48));
        setContentPane(panel);
    }

    public void showMessage(Component parent) {
        showMessage(parent, 0, 0, parent.getWidth(), parent.getHeight());
    }

    public void showMessage(Component parent, int xOffset, int yOffset, int gameAreaWidth, int gameAreaHeight) {
        if (currentMessage != null) {
            currentMessage.dispose();
            if (currentMessage.fadeTimer != null) currentMessage.fadeTimer.stop();
        }
        currentMessage = this;
        pack();
        int x = parent.getX() + xOffset + (gameAreaWidth - getWidth()) / 2;
        int y = parent.getY() + yOffset + (gameAreaHeight - getHeight()) / 2;
        setLocation(x, y);
        setVisible(true);
        setOpacity(0f);
        opacity = 0f;
        fadeIn = true;
        fadeTimer = new Timer(FADE_INTERVAL, new ActionListener() {
            long start = System.currentTimeMillis();
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - start;
                if (fadeIn) {
                    opacity = Math.min(1f, elapsed / (float) FADE_DURATION);
                    setOpacity(opacity);
                    if (opacity >= 1f) {
                        fadeIn = false;
                        start = System.currentTimeMillis();
                    }
                } else if (elapsed >= SHOW_DURATION) {
                    // Start fade out
                    fadeTimer.stop();
                    Timer out = new Timer(FADE_INTERVAL, null);
                    out.addActionListener(new ActionListener() {
                        long fadeStart = System.currentTimeMillis();
                        @Override
                        public void actionPerformed(ActionEvent e2) {
                            long fadeElapsed = System.currentTimeMillis() - fadeStart;
                            opacity = Math.max(0f, 1f - fadeElapsed / (float) FADE_DURATION);
                            setOpacity(opacity);
                            if (opacity <= 0f) {
                                out.stop();
                                dispose();
                                if (currentMessage == ConfirmationMessage.this) currentMessage = null;
                            }
                        }
                    });
                    out.start();
                }
            }
        });
        fadeTimer.start();
    }
} 