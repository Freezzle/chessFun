package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.network.command.client.SearchingGameCommand;
import ch.claudedy.chess.system.GameType;
import ch.claudedy.chess.ui.listener.GameChoosenListener;
import ch.claudedy.chess.ui.manager.AIManager;
import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.manager.NetworkManager;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;

@Accessors(fluent = true)
public class AppScreen extends JFrame {

    // Main layer of the application
    private final JLayeredPane mainLayer;

    // UI Child
    private final ChooseGameScreen chooseGameScreen;
    private WaitingScreen waitingScreen;
    private ChessScreen chessScreen;

    public AppScreen() {
        mainLayer = new JLayeredPane();
        mainLayer.setPreferredSize(new Dimension(600, 700));
        mainLayer.setBounds(new Rectangle(0, 0, 600, 700));
        getContentPane().add(mainLayer);

        chooseGameScreen = new ChooseGameScreen();
        chooseGameScreen.addOnGameChoosenListener(new GameChoosenListener(this));

        mainLayer.add(chooseGameScreen, 0);
    }

    public void printWaitingPlayer() {
        NetworkManager.instance().client().send(new SearchingGameCommand().infoPlayer(GameManager.instance().player()));

        mainLayer.remove(chooseGameScreen);
        waitingScreen = new WaitingScreen();
        mainLayer.add(waitingScreen, 0);
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

                chessScreen = new ChessScreen();
                NetworkManager.instance().game(chessScreen);
                mainLayer.add(chessScreen, 0);
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

        mainLayer.remove(chooseGameScreen);
        chessScreen = new ChessScreen();
        mainLayer.add(chessScreen, 0);
    }
}
