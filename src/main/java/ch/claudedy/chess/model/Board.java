package ch.claudedy.chess.model;

import ch.claudedy.chess.model.enumeration.Color;
import ch.claudedy.chess.model.enumeration.MoveType;
import ch.claudedy.chess.model.enumeration.PieceType;
import ch.claudedy.chess.model.enumeration.Tile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@Getter
@Setter
@EqualsAndHashCode
public class Board implements Serializable {
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

    public boolean isWhiteTurn() {
        return this.currentPlayer.isWhite();
    }

    public Square getSquare(int x, int y) {
        return this.squares[x][y];
    }

    public Square getSquare(Tile tile) {
        return this.squares[tile.x()][tile.y()];
    }

    public void switchPlayer() {
        this.currentPlayer = isWhiteTurn() ? Color.BLACK : Color.WHITE;
    }

    public void movePiece(Tile start, Tile end, Character promote) {
        var startSquare = this.squares[start.x()][start.y()];
        var endSquare = this.squares[end.x()][end.y()];

        var pieceToMove = startSquare.piece();

        // Manage special moves cases
        this.manageKingMove(end, pieceToMove);
        this.manageRookMove(startSquare, pieceToMove);
        this.managePawnMove(end, startSquare, endSquare, pieceToMove, promote);

        if (!isWhiteTurn()) this.moves++; // Increment a full move

        this.fiftyRules++;

        // Reset fifty rules if capturing a enemy piece OR moving a pawn
        if (endSquare.piece() != null && this.currentPlayer != endSquare.piece().color() || PieceType.PAWN == pieceToMove.type()) {
            this.fiftyRules = 0;
        }

        endSquare.placePiece(pieceToMove.letter()); // Move the piece to the destination square
        startSquare.removePiece(); // Remove the piece from the source square
    }

    public List<Square> getAllPiecesSquares() {
        List<Square> squares = new ArrayList<>();
        squares.addAll(this.getSquaresAlivePieces(Color.WHITE));
        squares.addAll(this.getSquaresAlivePieces(Color.BLACK));

        return squares;
    }

    public List<Square> getSquaresAlivePieces(Color player) {
        List<Square> squares = new ArrayList<>();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {

                Piece piece = this.squares[x][y].piece();
                if (piece != null && piece.color() == player) squares.add(this.squares[x][y]);
            }
        }

        return squares;
    }

    public List<Piece> getAlivePieces(Color player) {
        return this.getSquaresAlivePieces(player).stream().map(Square::piece).collect(Collectors.toList());
    }

    private void managePawnMove(Tile end, Square startSquare, Square endSquare, Piece pieceToMove, Character promote) {
        if (PieceType.PAWN != pieceToMove.type()) {
            this.enPassant = null;
            return;
        }

        if (promote == null) promote = 'q';

        if (Math.abs(endSquare.tile().y() - startSquare.tile().y()) == 2) {
            // MOVE -> Pawn moved 2 cases
            int offset = pieceToMove.isWhitePiece() ? -1 : 1;
            this.enPassant = Tile.getEnum(end.x(), end.y() + offset);
        } else {
            if (this.enPassant != null) {
                if (this.enPassant == end) { // MOVE -> Pawn go to the enPassant tile

                    int offset = pieceToMove.isWhitePiece() ? -1 : 1;
                    getSquare(Tile.getEnum(end.x(), end.y() + offset)).removePiece();
                } else {
                    // Reset EnPassant
                    this.enPassant = null;
                }
            }
        }

        char charPromote = Character.toUpperCase(promote);
        if (endSquare.tile().y() == 7 && pieceToMove.isWhitePiece()) pieceToMove.promote(charPromote);
        if (endSquare.tile().y() == 0 && !pieceToMove.isWhitePiece()) pieceToMove.promote(charPromote);
    }

    private void manageRookMove(Square startSquare, Piece pieceToMove) {
        if (PieceType.ROOK != pieceToMove.type()) return;

        if (pieceToMove.isWhitePiece() && startSquare.tile() == Tile.A1) this.canwQRoque = false;
        if (pieceToMove.isWhitePiece() && startSquare.tile() == Tile.H1) this.canwKRoque = false;
        if (!pieceToMove.isWhitePiece() && startSquare.tile() == Tile.A8) this.canbqRoque = false;
        if (!pieceToMove.isWhitePiece() && startSquare.tile() == Tile.H8) this.canbkRoque = false;
    }

    private void manageKingMove(Tile end, Piece pieceToMove) {
        if (PieceType.KING != pieceToMove.type()) return;

        if (pieceToMove.isWhitePiece()) {
            if (end == Tile.C1 && this.canwQRoque) {
                // MOVE -> Roque WHITE Queen side
                var rookQSide = this.getSquare(Tile.A1).piece();
                if (rookQSide != null && rookQSide.type() == PieceType.ROOK) {
                    this.getSquare(Tile.A1).removePiece();
                    this.getSquare(Tile.D1).placePiece(rookQSide.letter());
                }
            } else if (end == Tile.G1 && this.canwKRoque) {
                // MOVE -> Roque WHITE King side
                var rookKSide = this.getSquare(Tile.H1).piece();
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
                var rookQSide = this.getSquare(Tile.A8).piece();
                if (rookQSide != null && rookQSide.type() == PieceType.ROOK) {
                    this.getSquare(Tile.A8).removePiece();
                    this.getSquare(Tile.D8).placePiece(rookQSide.letter());
                }
            } else if (end == Tile.G8 && this.canbkRoque) {
                // MOVE -> Roque BLACK King side
                var rookKSide = this.getSquare(Tile.H8).piece();
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
        return isTileChecked(colorKing, getTileKing(colorKing));
    }

    public Tile getTileKing(Color colorKing) {
        Tile kingTile = null;

        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                var piece = squares[x][y].piece();
                if (piece != null && piece.type() == PieceType.KING && piece.color().isSameColor(colorKing)) {
                    kingTile = squares[x][y].tile();
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
                var piece = squares[x][y].piece();

                if (piece != null && piece.color() != allyColor) {
                    var threatens = piece.getMoves(this, squares[x][y].tile());
                    if (threatens.stream().filter(threat -> threat.type() == MoveType.MOVE_WITHOUT_CAPTURING
                            || threat.type() == MoveType.MOVE_WITH_CAPTURING
                            || threat.type() == MoveType.ONLY_THREAT
                            || threat.type() == MoveType.THREAT_ENEMY_KING)
                            .anyMatch(threat -> threat.destination() == tileToCheck)) {
                        isTileChecked = true;
                        break;
                    }
                }
            }

            if (isTileChecked) break;
        }

        return isTileChecked;
    }
}
