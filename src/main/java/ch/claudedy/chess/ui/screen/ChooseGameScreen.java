package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.ui.listener.GameChoosenListener;
import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.manager.NetworkManager;
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
public class ChooseGameScreen extends JPanel {

    List<GameChoosenListener> listeners = new ArrayList<>();

    JButton onlinePlay;

    public ChooseGameScreen() {
        setName("CHOOSE");
        setPreferredSize(new Dimension(600, 700));
        setBounds(new Rectangle(0, 0, 600, 700));

        JTextField name = new JTextField(GameManager.instance().player().name());
        name.setName("NAME");
        name.setPreferredSize(new Dimension(100, 25));
        add(name);

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

        JButton start = new JButton("Start the game");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameManager.instance().modeOnline(false);
                GameManager.instance().player().name(name.getText());
                listeners.forEach(listener -> listener.onGameChoosenListener());
            }
        });
        add(start);

        onlinePlay = new JButton("Start online");
        onlinePlay.setEnabled(false);
        onlinePlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameManager.instance().modeOnline(true);
                GameManager.instance().player().name(name.getText());
                listeners.forEach(listener -> listener.onGameChoosenListener());
            }
        });
        add(onlinePlay);

        checkForServer();
    }

    private void checkForServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                        NetworkManager.instance().startConnection();
                        onlinePlay.setEnabled(true);
                        break;
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
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
