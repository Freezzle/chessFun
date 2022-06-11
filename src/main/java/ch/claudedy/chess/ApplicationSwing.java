package ch.claudedy.chess;

import ch.claudedy.chess.ui.screen.MainUI;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

@Accessors(fluent = true)
public class ApplicationSwing extends JFrame {

    public static void main(String[] args) {
        MainUI main = new MainUI();
        main.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        main.pack();
        main.setResizable(true);
        main.setLocationRelativeTo(null);
        main.setVisible(true);
        main.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }
}
