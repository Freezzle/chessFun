package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.model.MoveStatus;
import ch.claudedy.chess.ui.screen.ChessScreen;

public class MoveFailedListener {

    private final ChessScreen chessScreen;

    public MoveFailedListener(ChessScreen chessScreen) {
        this.chessScreen = chessScreen;
    }

    public void onMoveFailedListener(MoveStatus status) {
        this.chessScreen.reset();
    }
}
