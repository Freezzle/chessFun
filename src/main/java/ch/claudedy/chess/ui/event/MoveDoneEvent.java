package ch.claudedy.chess.ui.event;

import ch.claudedy.chess.basis.MoveCommand;

public interface MoveDoneEvent {
    void onMoveDoneListener(MoveCommand command);
}
