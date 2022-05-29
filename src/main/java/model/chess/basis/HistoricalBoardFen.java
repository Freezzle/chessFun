package model.chess.basis;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import model.chess.actions.MoveCommand;

@Accessors(fluent = true)
@Getter
@Setter
public class HistoricalBoardFen {

    private String fen;
    private MoveCommand previousMove;
}
