package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.systems.GameType;
import ch.claudedy.chess.ui.delegate.AIDelegate;
import ch.claudedy.chess.ui.delegate.GameSettings;
import ch.claudedy.chess.ui.delegate.NetworkDelegate;
import ch.claudedy.chess.ui.listener.GameChoosenListener;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;

@Accessors(fluent = true)
public class MainUI extends JFrame {

    // Main layer of the application
    private final JLayeredPane mainLayer;

    // UI Child
    private final ChooseUI chooseUI;
    private ChessUI chessUI;

    public MainUI() {
        mainLayer = new JLayeredPane();
        mainLayer.setPreferredSize(new Dimension(600, 700));
        mainLayer.setBounds(new Rectangle(0, 0, 600, 700));
        getContentPane().add(mainLayer);

        chooseUI = new ChooseUI();
        chooseUI.addOnGameChoosenListener(new GameChoosenListener(this));

        mainLayer.add(chooseUI);
    }

    public void initGame() {
        if (GameSettings.getInstance().launchOnline()) {
            mainLayer.remove(chooseUI);
            NetworkDelegate.getInstance().startConnection();
            while (!NetworkDelegate.getInstance().hasGameStarted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            chessUI = new ChessUI();
            NetworkDelegate.getInstance().game(chessUI);
            mainLayer.add(chessUI, 0);
        } else {
            boolean playerOneComputer = GameSettings.getInstance().isPlayerOneComputer();
            boolean playerTwoComputer = GameSettings.getInstance().isPlayerTwoComputer();

            if (!playerOneComputer && !playerTwoComputer) {
                GameSettings.getInstance().gameType(GameType.PLAYER_V_PLAYER);
            } else if (playerOneComputer && !playerTwoComputer) {
                GameSettings.getInstance().gameType(GameType.COMPUTER_V_PLAYER);
                AIDelegate.startStockfish();
            } else if (!playerOneComputer && playerTwoComputer) {
                GameSettings.getInstance().gameType(GameType.PLAYER_V_COMPUTER);
                AIDelegate.startStockfish();
            } else {
                GameSettings.getInstance().gameType(GameType.COMPUTER_V_COMPUTER);
                AIDelegate.startStockfish();
            }

            mainLayer.remove(chooseUI);
            chessUI = new ChessUI();
            mainLayer.add(chessUI, 0);
        }
    }
}
