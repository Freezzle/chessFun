package ch.claudedy.chess.ui.screen.component;

import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;


@Accessors(fluent = true)
public class DialogInformation extends JDialog {
    private final JLabel textCounter;

    public DialogInformation(String textInformation, Long counter) {
        setLayout(new GridLayout(2, 1));
        setBounds(new Rectangle(500, 300, 400, 300));

        add(new JLabel(textInformation));

        textCounter = new JLabel("You will be redirect in " + counter + "s");
        add(textCounter);
    }

    public void setCounter(Long counter) {
        textCounter.setText("You will be redirect in " + counter + "s");
    }
}
