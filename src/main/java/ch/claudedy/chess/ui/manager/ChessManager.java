package ch.claudedy.chess.ui.manager;

import ch.claudedy.chess.model.Board;
import ch.claudedy.chess.model.Chess;
import ch.claudedy.chess.system.LoaderFromFile;
import ch.claudedy.chess.system.SystemSettings;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class ChessManager {
    private static ChessManager instance;

    @Getter
    private Chess chess;

    private ChessManager() {
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

    public static ChessManager instance() {
        if (instance == null) {
            instance = new ChessManager();
        }

        return instance;
    }

    public void startNewGame() {
        chess = LoaderFromFile.readFile(SystemSettings.BOARD);
    }
}
