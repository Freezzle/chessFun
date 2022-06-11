package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.basis.MoveCommand;
import ch.claudedy.chess.network.MoveClientCommand;
import ch.claudedy.chess.ui.delegate.ChessManager;
import ch.claudedy.chess.ui.delegate.GameManager;
import ch.claudedy.chess.ui.delegate.NetworkManager;
import ch.claudedy.chess.ui.screen.ChessUI;

public class MoveDoneListener {

    private final ChessUI chessUI;

    public MoveDoneListener(ChessUI chessUI) {
        this.chessUI = chessUI;
    }

    public void onMoveDoneListener(MoveCommand move, boolean fromLocalCommand) {
        this.chessUI.reset();

        if (GameManager.instance().modeOnline() && fromLocalCommand) {
            NetworkManager.instance().client().send(new MoveClientCommand().move(move));
        }

        if (ChessManager.instance().chess().gameStatus().isGameOver()) {
            return;
        }

        if (this.chessUI.playerWhite().isComputer() && ChessManager.instance().isWhiteTurn() || this.chessUI.playerBlack().isComputer() && !ChessManager.instance().isWhiteTurn()) {
            this.chessUI.launchComputerMove();
        }
    }
}
