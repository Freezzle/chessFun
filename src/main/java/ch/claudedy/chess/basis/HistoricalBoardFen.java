package ch.claudedy.chess.basis;

import ch.claudedy.chess.actions.MoveCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@Setter
public class HistoricalBoardFen {

    private String fen;
    private MoveCommand previousMove;
}
