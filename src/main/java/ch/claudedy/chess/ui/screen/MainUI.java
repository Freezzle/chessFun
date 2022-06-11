package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.network.SearchingGameCommand;
import ch.claudedy.chess.systems.GameType;
import ch.claudedy.chess.ui.delegate.AIManager;
import ch.claudedy.chess.ui.delegate.GameManager;
import ch.claudedy.chess.ui.delegate.NetworkManager;
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
    private WaitingUI waitingUI;
    private ChessUI chessUI;

    public MainUI() {
        mainLayer = new JLayeredPane();
        mainLayer.setPreferredSize(new Dimension(600, 700));
        mainLayer.setBounds(new Rectangle(0, 0, 600, 700));
        getContentPane().add(mainLayer);

        chooseUI = new ChooseUI();
        chooseUI.addOnGameChoosenListener(new GameChoosenListener(this));

        mainLayer.add(chooseUI, 0);
    }

    public void printWaitingPlayer() {
        NetworkManager.instance().client().send(new SearchingGameCommand().player(GameManager.instance().player()));

        mainLayer.remove(chooseUI);
        waitingUI = new WaitingUI();
        mainLayer.add(waitingUI, 0);
    }


    public void initGameOnline() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!GameManager.instance().hasGameStarted()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }

                chessUI = new ChessUI();
                NetworkManager.instance().game(chessUI);
                mainLayer.add(chessUI, 0);
            }
        }).start();
    }

    public void initGameLocal() {
        boolean playerOneComputer = GameManager.instance().isPlayerOneComputer();
        boolean playerTwoComputer = GameManager.instance().isPlayerTwoComputer();

        if (!playerOneComputer && !playerTwoComputer) {
            GameManager.instance().gameTypeLocal(GameType.PLAYER_V_PLAYER);
        } else if (playerOneComputer && !playerTwoComputer) {
            GameManager.instance().gameTypeLocal(GameType.COMPUTER_V_PLAYER);
            AIManager.instance().startStockfish();
        } else if (!playerOneComputer && playerTwoComputer) {
            GameManager.instance().gameTypeLocal(GameType.PLAYER_V_COMPUTER);
            AIManager.instance().startStockfish();
        } else {
            GameManager.instance().gameTypeLocal(GameType.COMPUTER_V_COMPUTER);
            AIManager.instance().startStockfish();
        }

        mainLayer.remove(chooseUI);
        chessUI = new ChessUI();
        mainLayer.add(chessUI, 0);
    }
}
