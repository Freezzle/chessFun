package ch.claudedy.chess.ui.event;

import ch.claudedy.chess.basis.MoveStatus;
import ch.claudedy.chess.ui.ChessUI;

public class MoveFailedListener implements MoveFailedEvent {

    private final ChessUI chessUI;

    public MoveFailedListener(ChessUI chessUI) {
        this.chessUI = chessUI;
    }

    public void onMoveFailedListener(MoveStatus status) {
        this.chessUI.reset();
    }
}
