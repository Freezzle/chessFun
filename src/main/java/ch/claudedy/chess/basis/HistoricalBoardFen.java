package ch.claudedy.chess.basis;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class HistoricalBoardFen {

    @Getter
    private String fen;

    @Getter
    private String onlyBoardFen;

    @Getter
    @Setter
    private MoveCommand previousMove;

    public HistoricalBoardFen fen(String fen) {
        this.fen = fen;

        this.onlyBoardFen = fen.split(" ")[0];

        return this;
    }
}
