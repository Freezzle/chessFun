package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.ui.listener.GameChoosenListener;
import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.manager.NetworkManager;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
public class ChooseTypeGameScreen extends JPanel {

    List<GameChoosenListener> listeners = new ArrayList<>();

    JButton onlinePlay;

    public ChooseTypeGameScreen() {
        setName("CHOOSE_TYPE_GAME");
        setPreferredSize(new Dimension(600, 700));
        setLayout(new GridLayout(3, 1));
        setBounds(new Rectangle(0, 0, 600, 700));

        // Name of the player
        JTextField name = new JTextField(GameManager.instance().player().name());
        name.setName("NAME");
        name.setPreferredSize(new Dimension(600, 30));
        add(name);

        // Button Local Game
        JButton localGame = new JButton("Local Game");
        localGame.setPreferredSize(new Dimension(600, 30));
        localGame.setBackground(Color.DARK_GRAY);
        localGame.setForeground(Color.WHITE);
        localGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameManager.instance().modeOnline(false);
                GameManager.instance().player().name(name.getText());
                listeners.forEach(listener -> listener.onGameLocalChoosenListener());
            }
        });
        add(localGame);


        // Button Local Game
        onlinePlay = new JButton("Online Game (Server offline)");
        onlinePlay.setPreferredSize(new Dimension(600, 30));
        onlinePlay.setBackground(Color.DARK_GRAY);
        onlinePlay.setForeground(Color.WHITE);
        onlinePlay.setEnabled(false);
        onlinePlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameManager.instance().modeOnline(true);
                GameManager.instance().player().name(name.getText());
                listeners.forEach(listener -> listener.launchGameListener());
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
                        onlinePlay.setText("Online Game");
                        break;
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }

    public void addOnGameChoosenListener(GameChoosenListener listener) {
        this.listeners.add(listener);
    }
}
