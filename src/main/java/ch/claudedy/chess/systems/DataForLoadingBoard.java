package ch.claudedy.chess.systems;

import lombok.Getter;
import lombok.experimental.Accessors;
import ch.claudedy.chess.actions.MoveCommand;

@Accessors(fluent = true)
@Getter
public class DataForLoadingBoard {

    private final String fen;
    private final MoveCommand previousMove;

    public DataForLoadingBoard(String fen, MoveCommand previousMove) {
        this.fen = fen;
        this.previousMove = previousMove;
    }
}
