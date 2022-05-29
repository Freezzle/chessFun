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
public class Piece {
    private Color color;
    private PieceType type;
    private Character letter;

    public Piece(char piece) {
        promote(piece);
    }

    public void promote(char piece) {
        this.type = PieceType.getFromAbrevTechnical(piece);
        this.color = PieceType.getColorFromCharacter(piece);
        this.letter = piece;
    }

    public List<Tile> getThreatens(Board board, Tile source) {
        List<Tile> moves = new ArrayList<>();
        Square[][] squares = board.squares();

        int x = source.x();
        int y = source.y();

        if (type.isTypeDiagonal()) {
            addDiagonaleMoves(moves, squares, x, y, true);
        }

        if (type.isTypeLinear()) {
            addLinearMoves(moves, squares, x, y, true);
        }

        if (type.isTypeL()) {
            addLMoves(moves, squares, x, y, true);
        }

        if (type.isTypeKing()) {
            addKingMoves(moves, board, x, y, true);
        }

        if (type.isTypePawn()) {
            addPawnMoves(moves, board, x, y, true);
        }

        return moves;
    }

    public List<Tile> getMoves(Board board, Tile source) {
        List<Tile> moves = new ArrayList<>();
        Square[][] squares = board.squares();

        int x = source.x();
        int y = source.y();

        if (type.isTypeDiagonal()) {
            addDiagonaleMoves(moves, squares, x, y, false);
        }

        if (type.isTypeLinear()) {
            addLinearMoves(moves, squares, x, y, false);
        }

        if (type.isTypeL()) {
            addLMoves(moves, squares, x, y, false);
        }

        if (type.isTypeKing()) {
            addKingMoves(moves, board, x, y, false);
        }

        if (type.isTypePawn()) {
            addPawnMoves(moves, board, x, y, false);
        }

        return moves;
    }

    private void addPawnMoves(List<Tile> moves, Board board, int x, int y, boolean withKingChecked) {
        int changerPromotion = this.color.isWhite() ? 1 : -1;

        // PROMOTION
        if (y + (changerPromotion) == 7 || y + (changerPromotion) == 0) {
            Square square = board.squares()[x][y + (changerPromotion)];
            if (square.piece() == null) {
                moves.add(Tile.getEnum(x, y + changerPromotion));
            }
        }

        // STANDARD
        int changer1StepMove = this.color.isWhite() ? 1 : -1;
        if (y + (changer1StepMove) > 0 && y + (changer1StepMove) < 7 && board.squares()[x][y + (changer1StepMove)].piece() == null) {
            moves.add(Tile.getEnum(x, y + changer1StepMove));

            int changer2StepMove = this.color.isWhite() ? 2 : -2;
            if (this.color == Color.WHITE && board.squares()[x][1] == board.squares()[x][y] || this.color == Color.BLACK && board.squares()[x][6] == board.squares()[x][y]) {
                // BIG MOVE FOR FIRST TIME
                if (board.squares()[x][y + (changer2StepMove)].piece() == null) {
                    moves.add(Tile.getEnum(x, y + changer2StepMove));
                }
            }
        }

        // ATTACK MOVE RIGHT
        int changerAttack = this.color.isWhite() ? 1 : -1;
        if (x + 1 <= 7) {
            Square square = board.squares()[x + 1][y + changerAttack];
            Piece piece = square.piece();
            if ((piece != null && piece.color != this.color && (piece.type != PieceType.KING || piece.type == PieceType.KING && withKingChecked)) || (piece == null && board.enPassant() == square.tile())) {
                moves.add(Tile.getEnum(x + 1, y + changerAttack));
            }
        }

        // ATTACK MOVE LEFT
        if (x - 1 >= 0) {
            Square square = board.squares()[x - 1][y + changerAttack];
            Piece piece = square.piece();
            if ((piece != null && piece.color != this.color && (piece.type != PieceType.KING || piece.type == PieceType.KING && withKingChecked)) || (piece == null && board.enPassant() == square.tile())) {
                moves.add(Tile.getEnum(x - 1, y + changerAttack));
            }
        }
    }

    private void addLMoves(List<Tile> moves, Square[][] squares, int x, int y, boolean withKingChecked) {
        Square sourceSquare = squares[x][y];

        if (x + 2 <= 7 && y + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, squares[x + 2][y + 1], withKingChecked);

        if (x + 1 <= 7 && y + 2 <= 7)
            addMoveIfNecessary(moves, sourceSquare, squares[x + 1][y + 2], withKingChecked);

        if (x - 2 >= 0 && y + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, squares[x - 2][y + 1], withKingChecked);

        if (x - 1 >= 0 && y + 2 <= 7)
            addMoveIfNecessary(moves, sourceSquare, squares[x - 1][y + 2], withKingChecked);

        if (x - 1 >= 0 && y - 2 >= 0)
            addMoveIfNecessary(moves, sourceSquare, squares[x - 1][y - 2], withKingChecked);

        if (x - 2 >= 0 && y - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, squares[x - 2][y - 1], withKingChecked);

        if (x + 1 <= 7 && y - 2 >= 0)
            addMoveIfNecessary(moves, sourceSquare, squares[x + 1][y - 2], withKingChecked);

        if (x + 2 <= 7 && y - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, squares[x + 2][y - 1], withKingChecked);
    }

    private void addKingMoves(List<Tile> moves, Board board, int x, int y, boolean withKingChecked) {
        Square sourceSquare = board.squares()[x][y];

        if (x + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, board.squares()[x + 1][y], withKingChecked);

        if (x + 1 <= 7 && y + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, board.squares()[x + 1][y + 1], withKingChecked);

        if (y + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, board.squares()[x][y + 1], withKingChecked);

        if (x - 1 >= 0 && y + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, board.squares()[x - 1][y + 1], withKingChecked);

        if (x - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, board.squares()[x - 1][y], withKingChecked);

        if (x - 1 >= 0 && y - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, board.squares()[x - 1][y - 1], withKingChecked);

        if (y - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, board.squares()[x][y - 1], withKingChecked);

        if (x + 1 <= 7 && y - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, board.squares()[x + 1][y - 1], withKingChecked);


        int ySide = color.isWhite() ? 0 : 7;

        // CASTLING QUEEN SIDE
        if ((color.isWhite() && board.canwQRoque()) || !color.isWhite() && board.canbqRoque()) {
            boolean canQueenCastling = true;
            for (int i = 1; i <= x - 1; i++) {
                if (board.squares()[i][ySide].piece() != null) {
                    canQueenCastling = false;
                    break;
                }
            }

            if (canQueenCastling) {
                moves.add(Tile.getEnum(2, ySide));
            }
        }

        // CASTLING KING SIDE
        if ((color.isWhite() && board.canwKRoque()) || !color.isWhite() && board.canbkRoque()) {
            boolean canKingCastling = true;
            for (int i = x + 1; i <= 7 - 1; i++) {
                if (board.squares()[i][ySide].piece() != null) {
                    canKingCastling = false;
                    break;
                }
            }

            if (canKingCastling) {
                moves.add(Tile.getEnum(6, ySide));
            }
        }
    }

    private void addLinearMoves(List<Tile> moves, Square[][] squares, int x, int y, boolean withKingChecked) {
        Square sourceSquare = squares[x][y];

        if (x < 7) {
            // RIGHT
            for (int i = x + 1; i <= 7; i++) {
                Square destinationSquare = squares[i][y];
                boolean hasAdded = addMoveIfNecessary(moves, sourceSquare, destinationSquare, withKingChecked);
                if (!hasAdded || destinationSquare.piece() != null) {
                    break;
                }
            }
        }

        if (x > 0) {
            // LEFT
            for (int i = x - 1; i >= 0; i--) {
                Square destinationSquare = squares[i][y];
                boolean hasAdded = addMoveIfNecessary(moves, sourceSquare, destinationSquare, withKingChecked);
                if (!hasAdded || destinationSquare.piece() != null) {
                    break;
                }
            }
        }

        if (y < 7) {
            // UP
            for (int i = y + 1; i <= 7; i++) {
                Square destinationSquare = squares[x][i];
                boolean hasAdded = addMoveIfNecessary(moves, sourceSquare, destinationSquare, withKingChecked);
                if (!hasAdded || destinationSquare.piece() != null) {
                    break;
                }
            }
        }

        if (y > 0) {
            // DOWN
            for (int i = y - 1; i >= 0; i--) {
                Square destinationSquare = squares[x][i];
                boolean hasAdded = addMoveIfNecessary(moves, sourceSquare, destinationSquare, withKingChecked);
                if (!hasAdded || destinationSquare.piece() != null) {
                    break;
                }
            }
        }
    }

    private void addDiagonaleMoves(List<Tile> moves, Square[][] squares, int x, int y, boolean withKingChecked) {
        Square sourceSquare = squares[x][y];

        // RIGHT UP
        for (int i = 1; i <= 7; i++) {
            if (x + i > 7 || y + i > 7) {
                break;
            }

            Square destinationSquare = squares[x + i][y + i];
            boolean hasAdded = addMoveIfNecessary(moves, sourceSquare, destinationSquare, withKingChecked);
            if (!hasAdded || destinationSquare.piece() != null) {
                break;
            }
        }

        // LEFT DOWN
        for (int i = 1; i <= 7; i++) {
            if (x - i < 0 || y - i < 0) {
                break;
            }

            Square destinationSquare = squares[x - i][y - i];
            boolean hasAdded = addMoveIfNecessary(moves, sourceSquare, destinationSquare, withKingChecked);
            if (!hasAdded || destinationSquare.piece() != null) {
                break;
            }
        }

        // RIGHT DOWN
        for (int i = 1; i <= 7; i++) {
            if (x + i > 7 || y - i < 0) {
                break;
            }

            Square destinationSquare = squares[x + i][y - i];
            boolean hasAdded = addMoveIfNecessary(moves, sourceSquare, destinationSquare, withKingChecked);
            if (!hasAdded || destinationSquare.piece() != null) {
                break;
            }
        }

        // LEFT UP
        for (int i = 1; i <= 7; i++) {
            if (x - i < 0 || y + i > 7) {
                break;
            }

            Square destinationSquare = squares[x - i][y + i];
            boolean hasAdded = addMoveIfNecessary(moves, sourceSquare, destinationSquare, withKingChecked);
            if (!hasAdded || destinationSquare.piece() != null) {
                break;
            }
        }
    }

    public boolean addMoveIfNecessary(List<Tile> moves, Square from, Square to, boolean withKingChecked) {
        if (to == null || from == null) {
            return false;
        }

        Piece sourcePiece = from.piece();
        Piece destPiece = to.piece();

        if (destPiece == null) {
            moves.add(to.tile());
            return true;
        } else if (destPiece.color() != sourcePiece.color() && !(destPiece.type == PieceType.KING)) {
            moves.add(to.tile());
            return true;
        } else if(destPiece.color() != sourcePiece.color() && destPiece.type == PieceType.KING && withKingChecked) {
            moves.add(to.tile());
            return true;
        }

        return false;
    }
}
