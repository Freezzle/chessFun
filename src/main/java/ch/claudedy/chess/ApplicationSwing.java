package ch.claudedy.chess;

import ch.claudedy.chess.ui.ChessUI;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

@Accessors(fluent = true)
public class ApplicationSwing extends JFrame {

    public static void main(String[] args) {
        ChessUI chessUI = new ChessUI();
        chessUI.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        chessUI.pack();
        chessUI.setResizable(true);
        chessUI.setLocationRelativeTo(null);
        chessUI.setVisible(true);
        chessUI.addWindowListener(new WindowListener() {
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
