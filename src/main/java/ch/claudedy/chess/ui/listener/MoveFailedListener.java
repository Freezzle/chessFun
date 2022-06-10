package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.basis.MoveStatus;
import ch.claudedy.chess.ui.ChessUI;

public class MoveFailedListener {

    private final ChessUI chessUI;

    public MoveFailedListener(ChessUI chessUI) {
        this.chessUI = chessUI;
    }

    public void onMoveFailedListener(MoveStatus status) {
        this.chessUI.reset();
    }
}
