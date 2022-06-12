package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.ui.listener.GameChoosenListener;
import ch.claudedy.chess.ui.manager.GameManager;
import lombok.experimental.Accessors;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
public class LocalGameScreen extends JPanel {

    List<GameChoosenListener> listeners = new ArrayList<>();

    public LocalGameScreen() {
        setName("CHOOSE");
        setPreferredSize(new Dimension(600, 700));
        setBounds(new Rectangle(0, 0, 600, 700));

        // Player White
        JList<String> playerOneList = new JList<>(getListData(true));
        playerOneList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        playerOneList.setSelectedIndex(0);
        playerOneList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                GameManager.instance().isPlayerOneComputer(playerOneList.getSelectedValue().equals("COMPUTER"));
            }
        });
        add(playerOneList);

        // Player Black
        JList<String> playerTwoList = new JList<>(getListData(false));
        playerTwoList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        playerTwoList.setSelectedIndex(0);
        playerTwoList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                GameManager.instance().isPlayerTwoComputer(playerTwoList.getSelectedValue().equals("COMPUTER"));
            }
        });
        add(playerTwoList);


        // Button to start the game
        JButton start = new JButton("Start the game");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameManager.instance().modeOnline(false);
                listeners.forEach(listener -> listener.launchGameListener());
            }
        });
        add(start);
    }

    private String[] getListData(boolean isPlayerOne) {
        if (isPlayerOne) {
            return GameManager.instance().isPlayerOneComputer() ? new String[]{"COMPUTER", "HUMAN"} : new String[]{"HUMAN", "COMPUTER"};
        } else {
            return GameManager.instance().isPlayerTwoComputer() ? new String[]{"COMPUTER", "HUMAN"} : new String[]{"HUMAN", "COMPUTER"};
        }
    }

    public void addOnGameChoosenListener(GameChoosenListener listener) {
        this.listeners.add(listener);
    }
}
