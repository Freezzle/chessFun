package ch.claudedy.chess.ui.manager;

import ch.claudedy.chess.model.Board;
import ch.claudedy.chess.model.Chess;
import ch.claudedy.chess.system.GameType;
import ch.claudedy.chess.system.LoaderFromFile;
import ch.claudedy.chess.system.SystemSettings;
import ch.claudedy.chess.ui.screen.model.InfoPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class GameManager {

    private static GameManager instance;

    @Getter
    @Setter
    private GameType gameTypeLocal = SystemSettings.GAME_TYPE;

    @Getter
    @Setter
    private boolean modeOnline = false;

    @Getter
    @Setter
    private boolean isPlayerOneComputer = gameTypeLocal == GameType.COMPUTER_V_COMPUTER || gameTypeLocal == GameType.COMPUTER_V_PLAYER;

    @Getter
    @Setter
    private boolean isPlayerTwoComputer = gameTypeLocal == GameType.COMPUTER_V_COMPUTER || gameTypeLocal == GameType.PLAYER_V_COMPUTER;

    @Getter
    @Setter
    private InfoPlayer player = new InfoPlayer().name("Dylan Claude").isComputer(false);

    private boolean gameStarted = false;

    @Getter
    private Chess chess;

    private GameManager() {
    }

    public static GameManager instance() {
        if (instance == null) {
            instance = new GameManager();
        }

        return instance;
    }

    public void setGameStarted(boolean gameStarted) {
        instance.gameStarted = gameStarted;
    }

    public boolean hasGameStarted() {
        return instance.gameStarted;
    }

    public Board currentBoard() {
        if (chess == null) {
            startNewGame();
        }
        return chess.currentBoard();
    }

    public boolean isWhiteTurn() {
        if (chess == null) {
            startNewGame();
        }
        return chess.currentBoard().isWhiteTurn();
    }

    public void startNewGame() {
        chess = LoaderFromFile.readFile(SystemSettings.BOARD);
    }
}
