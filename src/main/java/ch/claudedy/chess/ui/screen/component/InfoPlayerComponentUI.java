package ch.claudedy.chess.ui.screen.component;

import ch.claudedy.chess.model.Board;
import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.screen.model.InfoPlayer;
import ch.claudedy.chess.util.Calculator;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;


@Accessors(fluent = true)
public class InfoPlayerComponentUI extends JPanel {
    private final InfoPlayer player;

    public InfoPlayerComponentUI(InfoPlayer player) {
        this.player = player;
        setName("INFORMATION_" + player.color());
        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension(600, 50));
        setBounds(new Rectangle(0, 0, 600, 50));
    }

    public void updateInfoPlayer() {
        if (getComponentCount() != 0) {
            removeAll();
        }

        Board currentBoard = GameManager.instance().currentBoard();

        JTextField textPlayer = new JTextField(player.name() + (player.color().isSameColor(currentBoard.currentPlayer()) ? " - your turn" : ""));
        textPlayer.setName("PLAYER" + player.color());
        textPlayer.setFont(new Font("Arial", Font.BOLD, 12));
        textPlayer.setBounds(new Rectangle(600, 50));
        textPlayer.setBackground(java.awt.Color.WHITE);
        textPlayer.setForeground(java.awt.Color.BLACK);
        textPlayer.setMargin(new Insets(4, 8, 2, 2));
        add(textPlayer);

        JTextField textRemovedPieces = new JTextField(Calculator.giveRemovedPieces(currentBoard, player.color().reverseColor()));
        textRemovedPieces.setName("ENNEMY_" + player.color().reverseColor() + "_PIECE_CAPTURED");
        textRemovedPieces.setFont(new Font("Arial", Font.BOLD, 12));
        textRemovedPieces.setBounds(new Rectangle(600, 50));
        textRemovedPieces.setBackground(java.awt.Color.WHITE);
        textRemovedPieces.setForeground(java.awt.Color.BLACK);
        textRemovedPieces.setMargin(new Insets(4, 8, 2, 2));
        add(textRemovedPieces);

        doLayout();
    }
}
