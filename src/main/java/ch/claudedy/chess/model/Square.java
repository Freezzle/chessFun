package ch.claudedy.chess.model;

import ch.claudedy.chess.model.enumeration.Tile;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(fluent = true)
@Getter
public class Square implements Serializable {
    private final Tile tile;
    private Piece piece;

    public Square(final Tile tile) {
        this.tile = tile;
        this.piece = null;
    }

    public void placePiece(final char piece) {
        this.piece = new Piece(piece);
    }

    public void removePiece() {
        this.piece = null;
    }

    public Character printSquare() {
        return this.piece != null ? this.piece.letter() : ' ';
    }
}
