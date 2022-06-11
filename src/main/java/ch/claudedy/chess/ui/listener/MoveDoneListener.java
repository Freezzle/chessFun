package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.basis.MoveCommand;
import ch.claudedy.chess.network.MoveClientCommand;
import ch.claudedy.chess.ui.delegate.ChessDelegate;
import ch.claudedy.chess.ui.delegate.NetworkDelegate;
import ch.claudedy.chess.ui.screen.ChessUI;

public class MoveDoneListener {

    private final ChessUI chessUI;

    public MoveDoneListener(ChessUI chessUI) {
        this.chessUI = chessUI;
    }

    public void onMoveDoneListener(MoveCommand move, boolean fromLocalCommand) {
        this.chessUI.reset();

        if (ChessDelegate.chess().gameStatus().isGameOver()) {
            return;
        }

        if (NetworkDelegate.getInstance().isModeOnline() && fromLocalCommand) {
            NetworkDelegate.getInstance().client().send(new MoveClientCommand().move(move));
        }

        if (this.chessUI.playerWhite().isComputer() && ChessDelegate.isWhiteTurn() || this.chessUI.playerBlack().isComputer() && !ChessDelegate.isWhiteTurn()) {
            this.chessUI.launchComputerMove();
        }
    }
}
