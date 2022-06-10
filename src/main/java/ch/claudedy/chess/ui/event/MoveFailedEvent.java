package ch.claudedy.chess.ui.event;

import ch.claudedy.chess.basis.MoveStatus;

public interface MoveFailedEvent {
    void onMoveFailedListener(MoveStatus status);
}
