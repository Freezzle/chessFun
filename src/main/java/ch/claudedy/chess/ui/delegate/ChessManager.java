package ch.claudedy.chess.ui.delegate;

import ch.claudedy.chess.basis.Board;
import ch.claudedy.chess.basis.Chess;
import ch.claudedy.chess.systems.LoaderFromFile;
import ch.claudedy.chess.systems.SystemConfig;
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
        chess = LoaderFromFile.readFile(SystemConfig.BOARD);
    }
}
