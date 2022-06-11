package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.basis.MoveCommand;
import ch.claudedy.chess.ui.delegate.ChessDelegate;
import ch.claudedy.chess.ui.screen.ChessUI;

public class MoveDoneListener {

    private final ChessUI chessUI;

    public MoveDoneListener(ChessUI chessUI) {
        this.chessUI = chessUI;
    }

    public void onMoveDoneListener(MoveCommand move) {
        this.chessUI.reset();

        if (ChessDelegate.chess().gameStatus().isGameOver()) {
            return;
        }

        if (this.chessUI.playerWhite().isComputer() && ChessDelegate.isWhiteTurn() || this.chessUI.playerBlack().isComputer() && !ChessDelegate.isWhiteTurn()) {
            this.chessUI.launchComputerMove();
        }
    }
}
