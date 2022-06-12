package ch.claudedy.chess.model;

import ch.claudedy.chess.model.enumeration.Color;
import ch.claudedy.chess.model.enumeration.MoveType;
import ch.claudedy.chess.model.enumeration.PieceType;
import ch.claudedy.chess.model.enumeration.Tile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
public class Piece implements Comparable<Piece>, Serializable {
    private Color color;
    private PieceType type;
    private Character letter;

    public Piece(char piece) {
        promote(piece);
    }

    public Piece(String piece) {
        promote(piece.charAt(0));
    }

    public void promote(char piece) {
        this.type = PieceType.getFromAbrevTechnical(piece);
        this.color = PieceType.getColorFromCharacter(piece);
        this.letter = piece;
    }

    public boolean isWhitePiece() {
        return this.color.isWhite();
    }

    public List<PossibleMove> getMoves(Board board, Tile source) {
        List<PossibleMove> moves = new ArrayList<>();
        Square[][] squares = board.squares();

        int x = source.x();
        int y = source.y();

        if (type.canMoveDiagonally()) {
            addDiagonaleMoves(moves, squares, x, y);
        }

        if (type.canMoveLinearly()) {
            addLinearMoves(moves, squares, x, y);
        }

        if (type.canMoveInLShape()) {
            addLMoves(moves, squares, x, y);
        }

        if (type.canOnlyMoveOneCase()) {
            addKingMoves(moves, board, x, y);
        }

        if (type.canMoveOnlyForward()) {
            addPawnMoves(moves, board, x, y);
        }

        return moves;
    }

    private void addPawnMoves(List<PossibleMove> moves, Board board, int x, int y) {
        int changerPromotion = isWhitePiece() ? 1 : -1;

        // PROMOTION
        if (y + (changerPromotion) == 7 || y + (changerPromotion) == 0) {
            Square square = board.getSquare(x, y + (changerPromotion));
            if (square.piece() == null) {
                moves.add(new PossibleMove(Tile.getEnum(x, y + changerPromotion), MoveType.PROMOTE));
            }
        }

        // STANDARD
        int changer1StepMove = isWhitePiece() ? 1 : -1;
        if (y + (changer1StepMove) > 0 && y + (changer1StepMove) < 7 && board.getSquare(x, y + (changer1StepMove)).piece() == null) {
            moves.add(new PossibleMove(Tile.getEnum(x, y + changer1StepMove), MoveType.MOVE));

            int changer2StepMove = isWhitePiece() ? 2 : -2;
            if (this.color == Color.WHITE && board.getSquare(x, 1) == board.getSquare(x, y) || this.color == Color.BLACK && board.getSquare(x, 6) == board.getSquare(x, y)) {
                // BIG MOVE FOR FIRST TIME
                if (board.getSquare(x, y + (changer2StepMove)).piece() == null) {
                    moves.add(new PossibleMove(Tile.getEnum(x, y + changer2StepMove), MoveType.MOVE));
                }
            }
        }

        // ATTACK MOVE RIGHT
        int changerAttack = isWhitePiece() ? 1 : -1;
        if (x + 1 <= 7 && y + changerAttack <= 7 && y + changerAttack >= 0) {
            Square square = board.getSquare(x + 1, y + changerAttack);

            Piece piece = square.piece();
            if (piece == null && board.enPassant() == square.tile()) {
                moves.add(new PossibleMove(square.tile(), MoveType.EN_PASSANT));
            } else if (piece != null && piece.color != this.color) {
                if (piece.type == PieceType.KING) {
                    moves.add(new PossibleMove(square.tile(), MoveType.THREAT_ENEMY_KING));
                } else {
                    moves.add(new PossibleMove(square.tile(), MoveType.THREAT));
                }
            }
        }

        // ATTACK MOVE LEFT
        if (x - 1 >= 0 && y + changerAttack <= 7 && y + changerAttack >= 0) {
            Square square = board.getSquare(x - 1, y + changerAttack);
            Piece piece = square.piece();

            if (piece == null && board.enPassant() == square.tile()) {
                moves.add(new PossibleMove(square.tile(), MoveType.EN_PASSANT));
            } else if (piece != null && piece.color != this.color) {
                if (piece.type == PieceType.KING) {
                    moves.add(new PossibleMove(square.tile(), MoveType.THREAT_ENEMY_KING));
                } else {
                    moves.add(new PossibleMove(square.tile(), MoveType.THREAT));
                }
            }
        }
    }

    private void addLMoves(List<PossibleMove> moves, Square[][] squares, int x, int y) {
        Square sourceSquare = squares[x][y];

        if (x + 2 <= 7 && y + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, squares[x + 2][y + 1]);

        if (x + 1 <= 7 && y + 2 <= 7)
            addMoveIfNecessary(moves, sourceSquare, squares[x + 1][y + 2]);

        if (x - 2 >= 0 && y + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, squares[x - 2][y + 1]);

        if (x - 1 >= 0 && y + 2 <= 7)
            addMoveIfNecessary(moves, sourceSquare, squares[x - 1][y + 2]);

        if (x - 1 >= 0 && y - 2 >= 0)
            addMoveIfNecessary(moves, sourceSquare, squares[x - 1][y - 2]);

        if (x - 2 >= 0 && y - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, squares[x - 2][y - 1]);

        if (x + 1 <= 7 && y - 2 >= 0)
            addMoveIfNecessary(moves, sourceSquare, squares[x + 1][y - 2]);

        if (x + 2 <= 7 && y - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, squares[x + 2][y - 1]);
    }

    private void addKingMoves(List<PossibleMove> moves, Board board, int x, int y) {
        Square sourceSquare = board.getSquare(x, y);

        if (x + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, board.getSquare(x + 1, y));

        if (x + 1 <= 7 && y + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, board.getSquare(x + 1, y + 1));

        if (y + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, board.getSquare(x, y + 1));

        if (x - 1 >= 0 && y + 1 <= 7)
            addMoveIfNecessary(moves, sourceSquare, board.getSquare(x - 1, y + 1));

        if (x - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, board.getSquare(x - 1, y));

        if (x - 1 >= 0 && y - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, board.getSquare(x - 1, y - 1));

        if (y - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, board.getSquare(x, y - 1));

        if (x + 1 <= 7 && y - 1 >= 0)
            addMoveIfNecessary(moves, sourceSquare, board.getSquare(x + 1, y - 1));


        int ySide = isWhitePiece() ? 0 : 7;

        // CASTLING QUEEN SIDE
        if ((isWhitePiece() && board.canwQRoque()) || !isWhitePiece() && board.canbqRoque()) {
            boolean canQueenCastling = true;
            if (board.getSquare(0, ySide).piece() != null && board.getSquare(0, ySide).piece().type == PieceType.ROOK) {
                for (int i = 1; i <= x - 1; i++) {
                    if (board.getSquare(i, ySide).piece() != null) {
                        canQueenCastling = false;
                        break;
                    }
                }
            } else {
                canQueenCastling = false;
            }

            if (canQueenCastling) {
                moves.add(new PossibleMove(Tile.getEnum(2, ySide), MoveType.CASTLE));
            }
        }

        // CASTLING KING SIDE
        if ((isWhitePiece() && board.canwKRoque()) || !isWhitePiece() && board.canbkRoque()) {
            boolean canKingCastling = true;
            if (board.getSquare(7, ySide).piece() != null && board.getSquare(7, ySide).piece().type == PieceType.ROOK) {
                for (int i = x + 1; i <= 7 - 1; i++) {
                    if (board.getSquare(i, ySide).piece() != null) {
                        canKingCastling = false;
                        break;
                    }
                }
            } else {
                canKingCastling = false;
            }

            if (canKingCastling) {
                moves.add(new PossibleMove(Tile.getEnum(6, ySide), MoveType.CASTLE));
            }
        }
    }

    private void addLinearMoves(List<PossibleMove> moves, Square[][] squares, int x, int y) {
        Square sourceSquare = squares[x][y];

        if (x != 7) {
            // RIGHT
            for (int i = x + 1; i <= 7; i++) {
                MoveType moveTypeAdded = addMoveIfNecessary(moves, sourceSquare, squares[i][y]);
                if (mustStop(moveTypeAdded)) {
                    break;
                }
            }
        }

        if (x != 0) {
            // LEFT
            for (int i = x - 1; i >= 0; i--) {
                MoveType moveTypeAdded = addMoveIfNecessary(moves, sourceSquare, squares[i][y]);
                if (mustStop(moveTypeAdded)) {
                    break;
                }
            }
        }

        if (y != 7) {
            // UP
            for (int i = y + 1; i <= 7; i++) {
                MoveType moveTypeAdded = addMoveIfNecessary(moves, sourceSquare, squares[x][i]);
                if (mustStop(moveTypeAdded)) {
                    break;
                }
            }
        }

        if (y != 0) {
            // DOWN
            for (int i = y - 1; i >= 0; i--) {
                MoveType moveTypeAdded = addMoveIfNecessary(moves, sourceSquare, squares[x][i]);
                if (mustStop(moveTypeAdded)) {
                    break;
                }
            }
        }
    }

    private void addDiagonaleMoves(List<PossibleMove> moves, Square[][] squares, int x, int y) {
        Square sourceSquare = squares[x][y];

        // RIGHT UP
        if (x != 7 && y != 7) {
            for (int i = 1; i <= 7; i++) {
                if (x + i > 7 || y + i > 7) {
                    break;
                }

                MoveType moveTypeAdded = addMoveIfNecessary(moves, sourceSquare, squares[x + i][y + i]);
                if (mustStop(moveTypeAdded)) {
                    break;
                }
            }
        }

        // LEFT DOWN
        if (x != 0 && y != 0) {
            for (int i = 1; i <= 7; i++) {
                if (x - i < 0 || y - i < 0) {
                    break;
                }

                MoveType moveTypeAdded = addMoveIfNecessary(moves, sourceSquare, squares[x - i][y - i]);
                if (mustStop(moveTypeAdded)) {
                    break;
                }
            }
        }

        // RIGHT DOWN
        if (x != 7 && y != 0) {
            for (int i = 1; i <= 7; i++) {
                if (x + i > 7 || y - i < 0) {
                    break;
                }

                MoveType moveTypeAdded = addMoveIfNecessary(moves, sourceSquare, squares[x + i][y - i]);
                if (mustStop(moveTypeAdded)) {
                    break;
                }
            }
        }

        // LEFT UP
        if (x != 0 && y != 7) {
            for (int i = 1; i <= 7; i++) {
                if (x - i < 0 || y + i > 7) {
                    break;
                }

                MoveType moveTypeAdded = addMoveIfNecessary(moves, sourceSquare, squares[x - i][y + i]);
                if (mustStop(moveTypeAdded)) {
                    break;
                }
            }
        }
    }

    public MoveType addMoveIfNecessary(List<PossibleMove> moves, Square from, Square to) {
        if (to == null || from == null) {
            return null;
        }

        Piece sourcePiece = from.piece();
        Piece destPiece = to.piece();

        PossibleMove possibleMove;

        if (destPiece == null) {
            possibleMove = new PossibleMove(to.tile(), MoveType.MOVE);
        } else if (destPiece.color() != sourcePiece.color() && !(destPiece.type == PieceType.KING)) {
            possibleMove = new PossibleMove(to.tile(), MoveType.THREAT);
        } else if (destPiece.color() != sourcePiece.color() && destPiece.type == PieceType.KING) {
            possibleMove = new PossibleMove(to.tile(), MoveType.THREAT_ENEMY_KING);
        } else {
            possibleMove = null;
        }

        if (possibleMove != null) {
            moves.add(possibleMove);
            return possibleMove.type();
        }

        return null;
    }

    public boolean mustStop(MoveType moveTypeAdded) {
        return moveTypeAdded == null || moveTypeAdded == MoveType.THREAT || moveTypeAdded == MoveType.THREAT_ENEMY_KING;
    }

    @Override
    public int compareTo(Piece o) {
        if (this.type.value() > o.type.value()) {
            return -1;
        } else if (this.type.value() < o.type.value()) {
            return 1;
        } else {
            if (this.type == PieceType.BISHOP) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
