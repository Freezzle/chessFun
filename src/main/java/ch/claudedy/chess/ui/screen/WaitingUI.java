package ch.claudedy.chess.ui.screen;

import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;

@Accessors(fluent = true)
public class WaitingUI extends JPanel {
    public WaitingUI() {
        setName("WAITING");
        setPreferredSize(new Dimension(600, 700));
        setBounds(new Rectangle(0, 0, 600, 700));

        JTextField name = new JTextField("Waiting for an opponent...");
        name.setName("WAITING_TEXT");
        add(name);
    }
}
