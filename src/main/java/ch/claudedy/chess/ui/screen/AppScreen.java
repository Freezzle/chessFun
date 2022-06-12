package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.network.command.client.SearchingGameCommand;
import ch.claudedy.chess.system.GameType;
import ch.claudedy.chess.ui.listener.GameChoosenListener;
import ch.claudedy.chess.ui.listener.GameEndedListener;
import ch.claudedy.chess.ui.manager.AIManager;
import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.manager.NetworkManager;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;

@Accessors(fluent = true)
public class AppScreen extends JFrame {

    private static AppScreen instance;

    // Main layer of the application
    private final JLayeredPane mainLayer;

    // UI Child
    private ChooseTypeGameScreen chooseTypeGameScreen;
    private LocalGameScreen localGameScreen;
    private OnlineWaitingScreen onlineWaitingScreen;
    private ChessScreen chessScreen;

    public AppScreen() {
        instance = this;
        mainLayer = new JLayeredPane();
        mainLayer.setPreferredSize(new Dimension(600, 700));
        mainLayer.setBounds(new Rectangle(0, 0, 600, 700));
        getContentPane().add(mainLayer);

        // Make a choice between Online | Local Game
        chooseTypeGameScreen = new ChooseTypeGameScreen();
        chooseTypeGameScreen.addOnGameChoosenListener(new GameChoosenListener(this));

        mainLayer.add(chooseTypeGameScreen, 0);
    }

    public void startWaitingGameOnline() {
        NetworkManager.instance().client().send(new SearchingGameCommand().infoPlayer(GameManager.instance().player()));

        mainLayer.remove(chooseTypeGameScreen);
        onlineWaitingScreen = new OnlineWaitingScreen();
        mainLayer.add(onlineWaitingScreen, 0);
    }

    public void startGameOnline() {
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
                chessScreen.addGameEndedListener(new GameEndedListener(instance));
                NetworkManager.instance().game(chessScreen);
                mainLayer.add(chessScreen, 0);
            }
        }).start();
    }

    public void startChoosingLocalGame() {
        mainLayer.remove(chooseTypeGameScreen);
        localGameScreen = new LocalGameScreen();
        localGameScreen.addOnGameChoosenListener(new GameChoosenListener(this));
        mainLayer.add(localGameScreen, 0);
    }

    public void startGameLocal() {
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

        mainLayer.remove(chooseTypeGameScreen);
        chessScreen = new ChessScreen();
        chessScreen.addGameEndedListener(new GameEndedListener(instance));
        mainLayer.add(chessScreen, 0);
    }

    public void comeBackMenu() {
        mainLayer.removeAll();
        // Make a choice between Online | Local Game
        chooseTypeGameScreen = new ChooseTypeGameScreen();
        chooseTypeGameScreen.addOnGameChoosenListener(new GameChoosenListener(this));

        mainLayer.add(chooseTypeGameScreen, 0);
    }
}
