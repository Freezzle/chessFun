package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.ui.delegate.GameSettings;
import ch.claudedy.chess.ui.delegate.NetworkDelegate;
import ch.claudedy.chess.ui.listener.GameChoosenListener;
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
public class ChooseUI extends JPanel {

    List<GameChoosenListener> listeners = new ArrayList<>();

    JButton onlinePlay;

    public ChooseUI() {
        setName("CHOOSE");
        setPreferredSize(new Dimension(600, 700));
        setBounds(new Rectangle(0, 0, 600, 700));

        JTextField name = new JTextField(GameSettings.getInstance().name());
        name.setName("NAME");
        add(name);

        JList<String> playerOneList = new JList<>(getListData(true));
        playerOneList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        playerOneList.setSelectedIndex(0);
        playerOneList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                GameSettings.getInstance().isPlayerOneComputer(playerOneList.getSelectedValue().equals("COMPUTER"));
            }
        });
        add(playerOneList);

        JList<String> playerTwoList = new JList<>(getListData(false));
        playerTwoList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        playerTwoList.setSelectedIndex(0);
        playerTwoList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                GameSettings.getInstance().isPlayerTwoComputer(playerTwoList.getSelectedValue().equals("COMPUTER"));
            }
        });
        add(playerTwoList);

        JButton start = new JButton("Start the game");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameSettings.getInstance().launchOnline(false);
                GameSettings.getInstance().name(name.getText());
                listeners.forEach(listener -> listener.onGameChoosenListener());
            }
        });
        add(start);

        onlinePlay = new JButton("Start online");
        onlinePlay.setEnabled(false);
        onlinePlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameSettings.getInstance().launchOnline(true);
                GameSettings.getInstance().name(name.getText());
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
                        NetworkDelegate.getInstance().startConnection();
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
            return GameSettings.getInstance().isPlayerOneComputer() ? new String[]{"COMPUTER", "HUMAN"} : new String[]{"HUMAN", "COMPUTER"};
        } else {
            return GameSettings.getInstance().isPlayerTwoComputer() ? new String[]{"COMPUTER", "HUMAN"} : new String[]{"HUMAN", "COMPUTER"};
        }
    }

    public void addOnGameChoosenListener(GameChoosenListener listener) {
        this.listeners.add(listener);
    }
}
