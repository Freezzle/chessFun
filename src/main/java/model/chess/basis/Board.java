package model.chess.basis;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Setter
@Getter
@EqualsAndHashCode
public class Board {
    private final Square[][] squares;
    private Color currentPlayer;

    private boolean canwQRoque = false;
    private boolean canwKRoque = false;
    private boolean canbqRoque = false;
    private boolean canbkRoque = false;
    private Tile enPassant = null;
    private int fiftyRules;
    private int semiMoves;

    public Board() {
        this.squares = new Square[8][8];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                this.squares[x][y] = new Square(Tile.getEnum(x, y));
            }
        }
    }

    public Square get(Tile tile) {
        return this.squares[tile.x()][tile.y()];
    }

    public void switchPlayer() {
        this.currentPlayer = this.currentPlayer.isWhite() ? Color.BLACK : Color.WHITE;
    }

    public void movePiece(Tile start, Tile end) {

        Square startSquare = this.squares[start.x()][start.y()];
        Square endSquare = this.squares[end.x()][end.y()];

        Piece pieceToMove = startSquare.piece();

        if (pieceToMove.type() == PieceType.KING) {
            if (pieceToMove.color().isWhite()) {
                if (end == Tile.C1 && this.canwQRoque) {
                    Piece rookQSide = this.get(Tile.A1).piece();
                    this.get(Tile.A1).removePiece();
                    this.get(Tile.D1).placePiece(rookQSide.letter());
                } else if (end == Tile.G1 && this.canwKRoque) {
                    Piece rookKSide = this.get(Tile.H1).piece();
                    this.get(Tile.H1).removePiece();
                    this.get(Tile.F1).placePiece(rookKSide.letter());
                }
                this.canwKRoque = false;
                this.canwQRoque = false;
            } else {
                if (end == Tile.C8 && this.canbqRoque) {
                    Piece rookQSide = this.get(Tile.A8).piece();
                    this.get(Tile.A8).removePiece();
                    this.get(Tile.D8).placePiece(rookQSide.letter());
                } else if (end == Tile.G8 && this.canbkRoque) {
                    Piece rookKSide = this.get(Tile.H8).piece();
                    this.get(Tile.H8).removePiece();
                    this.get(Tile.F8).placePiece(rookKSide.letter());
                }
                this.canbkRoque = false;
                this.canbqRoque = false;
            }

            this.enPassant = null;
        } else if (pieceToMove.type() == PieceType.ROOK) {
            if (pieceToMove.color().isWhite()) {
                if (startSquare.tile() == Tile.A1) {
                    this.canwQRoque = false;
                } else if (startSquare.tile() == Tile.H1) {
                    this.canwKRoque = false;
                }
            } else {
                if (startSquare.tile() == Tile.A8) {
                    this.canbqRoque = false;
                } else if (startSquare.tile() == Tile.H8) {
                    this.canbkRoque = false;
                }
            }

            this.enPassant = null;
        } else if (pieceToMove.type() == PieceType.PAWN) {
            if (Math.abs(endSquare.tile().y() - startSquare.tile().y()) == 2) {
                if (pieceToMove.color().isWhite()) {
                    this.enPassant = Tile.getEnum(end.x(), end.y() - 1);
                } else {
                    this.enPassant = Tile.getEnum(end.x(), end.y() + 1);
                }
            } else if(this.enPassant != null) {
                if(pieceToMove.color().isWhite() && this.enPassant == end) {
                    // Remove pawn en passant
                    get(Tile.getEnum(end.x(), end.y() - 1)).removePiece();
                } else if(!pieceToMove.color().isWhite() && this.enPassant == end) {
                    // Remove pawn en passant
                    get(Tile.getEnum(end.x(), end.y() + 1)).removePiece();
                }
                this.enPassant = null;
            }
        } else {
            this.enPassant = null;
        }

        endSquare.placePiece(pieceToMove.letter());
        startSquare.removePiece();
    }
}
