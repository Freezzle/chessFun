package ch.claudedy.chess.ui.screen;

import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;

@Accessors(fluent = true)
public class OnlineWaitingScreen extends JPanel {
    public OnlineWaitingScreen() {
        setName("WAITING");
        setPreferredSize(new Dimension(600, 700));
        setLayout(new GridLayout(5, 1));
        setBounds(new Rectangle(0, 0, 600, 700));

        JTextField name = new JTextField("Waiting for an opponent...");
        name.setName("WAITING_TEXT");
        add(name);
    }
}
