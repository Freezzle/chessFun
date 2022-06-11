package ch.claudedy.chess.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(fluent = true)
public class HistoricalBoardFen implements Serializable {

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
