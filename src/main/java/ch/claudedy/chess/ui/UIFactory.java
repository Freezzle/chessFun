package ch.claudedy.chess.ui;

import javax.swing.*;
import java.awt.*;

public class UIFactory {
    public static void createTextField(JPanel parent, String name, String text, Color background, Color foreground) {
        JTextField panel = new JTextField(text);
        panel.setName(name);
        panel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.setBounds(new Rectangle(600, 50));
        panel.setBackground(background);
        panel.setForeground(foreground);
        panel.setMargin(new Insets(4, 8, 2, 2));
        parent.add(panel);
    }

    public static JPanel createPanel(String name, GridLayout layout, Dimension dimension, Rectangle bounds) {
        JPanel panel = new JPanel(layout);
        panel.setName(name);
        panel.setPreferredSize(dimension);
        panel.setBounds(bounds);

        return panel;
    }
}
