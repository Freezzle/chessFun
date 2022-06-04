package ch.claudedy.chess.basis;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

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
    private int fiftyRules = 0;
    private int moves = 1;

    public Board() {
        // Init the board
        this.squares = new Square[8][8];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                // Empty tiles
                this.squares[x][y] = new Square(Tile.getEnum(x, y));
            }
        }
    }

    public boolean isWhiteCurrentPlayer() {
        return this.currentPlayer.isWhite();
    }

    public Square get(Tile tile) {
        return this.squares[tile.x()][tile.y()];
    }

    public void switchPlayer() {
        this.currentPlayer = isWhiteCurrentPlayer() ? Color.BLACK : Color.WHITE;
    }

    public void movePiece(Tile start, Tile end, Character promote) {
        // Reset EnPassant
        this.enPassant = null;

        Square startSquare = this.squares[start.x()][start.y()];
        Square endSquare = this.squares[end.x()][end.y()];

        Piece pieceToMove = startSquare.piece();

        if (pieceToMove == null) {
            return;
        }

        // Special cases
        manageKingMove(end, pieceToMove);
        manageRookMove(startSquare, pieceToMove);
        managePawnMove(end, startSquare, endSquare, pieceToMove, promote);

        if (!isWhiteCurrentPlayer()) {
            this.moves++;
        }

        if (endSquare.piece() != null && this.currentPlayer != endSquare.piece().color() || PieceType.PAWN == pieceToMove.type()) {
            this.fiftyRules = 0;
        } else {
            this.fiftyRules++;
        }

        // Move the piece to the destination square
        endSquare.placePiece(pieceToMove.letter());
        // Remove the piece from the source square
        startSquare.removePiece();
    }

    public List<Piece> getAlivePieces(Color player) {
        List<Piece> pieces = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                // Empty tiles
                Piece piece = this.squares[x][y].piece();

                if (piece != null && piece.color() == player) {
                    pieces.add(piece);
                }
            }
        }

        return pieces;
    }

    private void managePawnMove(Tile end, Square startSquare, Square endSquare, Piece pieceToMove, Character promote) {
        if (PieceType.PAWN != pieceToMove.type()) {
            return;
        }

        if (promote == null) {
            promote = 'q';
        }

        if (Math.abs(endSquare.tile().y() - startSquare.tile().y()) == 2) {
            // MOVE -> Pawn moved 2 cases
            if (pieceToMove.isWhitePiece()) {
                this.enPassant = Tile.getEnum(end.x(), end.y() - 1);
            } else {
                this.enPassant = Tile.getEnum(end.x(), end.y() + 1);
            }
        } else if (this.enPassant != null) {
            // MOVE -> Pawn go to the enPassant tile
            if (this.enPassant == end) {
                if (pieceToMove.isWhitePiece()) {
                    get(Tile.getEnum(end.x(), end.y() - 1)).removePiece();
                } else {
                    get(Tile.getEnum(end.x(), end.y() + 1)).removePiece();
                }
            }
        } else if (endSquare.tile().y() == 7 && pieceToMove.isWhitePiece()) {
            // MOVE -> Promotion for WHITE to the top of the board
            pieceToMove.promote(Character.toUpperCase(promote));
        } else if (endSquare.tile().y() == 0 && !pieceToMove.isWhitePiece()) {
            // MOVE -> Promotion for BLACK to the bottom of the board
            pieceToMove.promote(Character.toLowerCase(promote));
        }
    }

    private void manageRookMove(Square startSquare, Piece pieceToMove) {
        if (PieceType.ROOK != pieceToMove.type()) {
            return;
        }

        if (pieceToMove.isWhitePiece()) {
            if (startSquare.tile() == Tile.A1) {
                // MOVE -> WHITE Rook Queen Side has moved from his starting position
                this.canwQRoque = false;
            } else if (startSquare.tile() == Tile.H1) {
                // MOVE -> WHITE Rook King Side has moved from his starting position
                this.canwKRoque = false;
            }
        } else {
            if (startSquare.tile() == Tile.A8) {
                // MOVE -> BLACK Rook Queen Side has moved from his starting position
                this.canbqRoque = false;
            } else if (startSquare.tile() == Tile.H8) {
                // MOVE -> BLACK Rook King Side has moved from his starting position
                this.canbkRoque = false;
            }
        }
    }

    private void manageKingMove(Tile end, Piece pieceToMove) {
        if (PieceType.KING != pieceToMove.type()) {
            return;
        }
        if (pieceToMove.isWhitePiece()) {
            if (end == Tile.C1 && this.canwQRoque) {
                // MOVE -> Roque WHITE Queen side
                Piece rookQSide = this.get(Tile.A1).piece();
                if (rookQSide != null) {
                    this.get(Tile.A1).removePiece();
                    this.get(Tile.D1).placePiece(rookQSide.letter());
                }
            } else if (end == Tile.G1 && this.canwKRoque) {
                // MOVE -> Roque WHITE King side
                Piece rookKSide = this.get(Tile.H1).piece();
                if (rookKSide != null) {
                    this.get(Tile.H1).removePiece();
                    this.get(Tile.F1).placePiece(rookKSide.letter());
                }
            }

            // No matter what, if the king move (castle or not), he can't do a roque anymore
            this.canwKRoque = false;
            this.canwQRoque = false;
        } else {
            if (end == Tile.C8 && this.canbqRoque) {
                // MOVE -> Roque BLACK Queen side
                Piece rookQSide = this.get(Tile.A8).piece();
                if (rookQSide != null) {
                    this.get(Tile.A8).removePiece();
                    this.get(Tile.D8).placePiece(rookQSide.letter());
                }
            } else if (end == Tile.G8 && this.canbkRoque) {
                // MOVE -> Roque BLACK King side
                Piece rookKSide = this.get(Tile.H8).piece();
                if (rookKSide != null) {
                    this.get(Tile.H8).removePiece();
                    this.get(Tile.F8).placePiece(rookKSide.letter());
                }
            }

            // No matter what, if the king move (castle or not), he can't do a roque anymore
            this.canbkRoque = false;
            this.canbqRoque = false;
        }
    }
}
