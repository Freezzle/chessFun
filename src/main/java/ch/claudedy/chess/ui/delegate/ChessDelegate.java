package ch.claudedy.chess.ui.delegate;

import ch.claudedy.chess.basis.Board;
import ch.claudedy.chess.basis.Chess;
import ch.claudedy.chess.systems.LoaderFromFile;
import ch.claudedy.chess.systems.SystemConfig;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class ChessDelegate {

    private static ChessDelegate instance;
    private Chess chess;

    private ChessDelegate() {
    }

    public static Chess chess() {
        return getInstance().chess;
    }

    public static Board currentBoard() {
        return chess().currentBoard();
    }

    public static boolean isWhiteTurn() {
        return currentBoard().isWhiteTurn();
    }

    private static ChessDelegate getInstance() {
        if (instance == null) {
            instance = new ChessDelegate();
        }

        return instance;
    }

    public static void startNewGame() {
        getInstance().chess = LoaderFromFile.readFile(SystemConfig.BOARD);
    }
}
