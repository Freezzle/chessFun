package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.ui.delegate.GameSettings;
import ch.claudedy.chess.ui.listener.GameChoosenListener;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
public class ChooseUI extends JPanel {

    List<GameChoosenListener> listeners = new ArrayList<>();

    public ChooseUI() {
        setName("CHOOSE");
        setPreferredSize(new Dimension(600, 700));
        setBounds(new Rectangle(0, 0, 600, 700));

        JCheckBox playerOne = new JCheckBox("White computer ?", GameSettings.getInstance().isPlayerOneComputer());
        playerOne.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameSettings.getInstance().isPlayerOneComputer(playerOne.isSelected());
            }
        });
        add(playerOne);

        JCheckBox playerTwo = new JCheckBox("Black computer ?", GameSettings.getInstance().isPlayerTwoComputer());
        playerTwo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameSettings.getInstance().isPlayerTwoComputer(playerTwo.isSelected());
            }
        });
        add(playerTwo);


        JButton start = new JButton("Start the game");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listeners.forEach(listener -> listener.onGameChoosenListener());
            }
        });
        add(start);
    }

    public void addOnGameChoosenListener(GameChoosenListener listener) {
        this.listeners.add(listener);
    }
}
