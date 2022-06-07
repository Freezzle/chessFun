package ch.claudedy.chess.basis;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
@Getter
@Setter
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

    public Square getSquare(Tile tile) {
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

        // Increment a full move
        if (!isWhiteCurrentPlayer()) {
            this.moves++;
        }

        // Reset fifty rules if capturing a enemy piece OR moving a pawn
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

    public List<Square> getAllPiecesSquares() {
        List<Square> squares = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                // Empty tiles
                Piece piece = this.squares[x][y].piece();

                if (piece != null) {
                    squares.add(this.squares[x][y]);
                }
            }
        }

        return squares;
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
                    getSquare(Tile.getEnum(end.x(), end.y() - 1)).removePiece();
                } else {
                    getSquare(Tile.getEnum(end.x(), end.y() + 1)).removePiece();
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
                Piece rookQSide = this.getSquare(Tile.A1).piece();
                if (rookQSide != null && rookQSide.type() == PieceType.ROOK) {
                    this.getSquare(Tile.A1).removePiece();
                    this.getSquare(Tile.D1).placePiece(rookQSide.letter());
                }
            } else if (end == Tile.G1 && this.canwKRoque) {
                // MOVE -> Roque WHITE King side
                Piece rookKSide = this.getSquare(Tile.H1).piece();
                if (rookKSide != null && rookKSide.type() == PieceType.ROOK) {
                    this.getSquare(Tile.H1).removePiece();
                    this.getSquare(Tile.F1).placePiece(rookKSide.letter());
                }
            }

            // No matter what, if the king move (castle or not), he can't do a roque anymore
            this.canwKRoque = false;
            this.canwQRoque = false;
        } else {
            if (end == Tile.C8 && this.canbqRoque) {
                // MOVE -> Roque BLACK Queen side
                Piece rookQSide = this.getSquare(Tile.A8).piece();
                if (rookQSide != null && rookQSide.type() == PieceType.ROOK) {
                    this.getSquare(Tile.A8).removePiece();
                    this.getSquare(Tile.D8).placePiece(rookQSide.letter());
                }
            } else if (end == Tile.G8 && this.canbkRoque) {
                // MOVE -> Roque BLACK King side
                Piece rookKSide = this.getSquare(Tile.H8).piece();
                if (rookKSide != null && rookKSide.type() == PieceType.ROOK) {
                    this.getSquare(Tile.H8).removePiece();
                    this.getSquare(Tile.F8).placePiece(rookKSide.letter());
                }
            }

            // No matter what, if the king move (castle or not), he can't do a roque anymore
            this.canbkRoque = false;
            this.canbqRoque = false;
        }
    }

    public boolean isKingChecked(Color colorKing) {
        // Search our king
        return isTileChecked(colorKing, getTileKing(colorKing));
    }

    public Tile getTileKing(Color colorKing) {
        Tile kingTile = null;
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                Piece piece = squares()[x][y].piece();
                if (piece != null && piece.type() == PieceType.KING && piece.color() == colorKing) {
                    kingTile = squares()[x][y].tile();
                }
            }
        }
        return kingTile;
    }

    public boolean isTileChecked(Color allyColor, Tile tileToCheck) {
        // Get all enemy pieces, and see if one is hitting our king
        boolean isTileChecked = false;
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                Piece piece = squares()[x][y].piece();
                if (piece != null && piece.color() != allyColor) {
                    List<PossibleMove> threatens = piece.getMoves(this, squares()[x][y].tile());
                    if (threatens.stream().anyMatch(threat -> threat.destination() == tileToCheck)) {
                        isTileChecked = true;
                        break;
                    }
                }
            }
        }

        return isTileChecked;
    }
}
