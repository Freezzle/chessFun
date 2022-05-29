package ch.claudedy.chess.basis;

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
