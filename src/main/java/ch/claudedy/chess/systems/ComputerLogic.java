package ch.claudedy.chess.systems;

import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.utils.FenUtils;

import java.util.List;

public class ComputerLogic {


    public static MoveCommand minimaxRoot(int depth, Chess chess) {
        float bestValue = -1000000f;
        MoveCommand bestMoveFound = null;
        List<MoveCommand> allMoves = chess.getAllMoves(chess.currentBoard().currentPlayer());
        Chess cloneChess = new Chess(FenUtils.boardToFen(chess.currentBoard()), chess.actualMove());

        for (MoveCommand currentMove : allMoves) {
            cloneChess.makeMove(currentMove);
            float currentValue = alphabeta(cloneChess, false, depth, -100000f, 100000f);
            cloneChess.rollbackPreviousState();

            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestMoveFound = currentMove;
            }
        }

        return bestMoveFound;
    }

    public static float alphabeta(Chess chess, boolean isMaximize, int depth, float valuePlayerMax, float valuePlayerMin) {

        if (depth == 0 || chess.status() != MoveFeedBack.RUNNING) {
            if (chess.status() == MoveFeedBack.CHECKMATED) {
                return isMaximize ? -999999999f : 999999999f;
            }

            return evaluateBoard(chess);
        }

        float bestValue;
        List<MoveCommand> allMoves = chess.getAllMoves(chess.currentBoard().currentPlayer());
        if (isMaximize) {
            bestValue = -1000000f;

            Chess cloneChess = new Chess(FenUtils.boardToFen(chess.currentBoard()), chess.actualMove());
            for (MoveCommand currentMove : allMoves) {
                cloneChess.makeMove(currentMove);
                bestValue = Math.max(bestValue, alphabeta(cloneChess, false, depth - 1, valuePlayerMax, valuePlayerMin));
                cloneChess.rollbackPreviousState();
                valuePlayerMax = Math.max(valuePlayerMax, bestValue);
                if (valuePlayerMax >= valuePlayerMin)
                    break;
            }


            return bestValue;
        } else {
            bestValue = 1000000f;

            Chess cloneChess = new Chess(FenUtils.boardToFen(chess.currentBoard()), chess.actualMove());
            for (MoveCommand currentMove : allMoves) {
                cloneChess.makeMove(currentMove);
                bestValue = Math.min(bestValue, alphabeta(cloneChess, true, depth - 1, valuePlayerMax, valuePlayerMin));
                cloneChess.rollbackPreviousState();
                valuePlayerMin = Math.min(valuePlayerMin, bestValue);
                if (valuePlayerMin <= valuePlayerMax)
                    break;
            }

            return bestValue;
        }
    }

    private static float evaluateBoard(Chess chess) {
        float totalValue = 0.0f;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                totalValue += getValueFromSquare(chess.currentBoard().squares()[x][y], chess.currentBoard().currentPlayer());
            }
        }

        return totalValue;
    }

    static float getValueFromSquare(Square square, Color player) {
        if (square.piece() == null) {
            return 0.0f;
        }

        Piece piece = square.piece();

        int x = !piece.color().isWhite() ? square.tile().x() : 7 - square.tile().x();
        int y = !piece.color().isWhite() ? square.tile().y() : 7 - square.tile().y();

        float value;

        if (piece.type() == PieceType.PAWN) {
            value = pawnEvalBlack[y][x];
        } else if (piece.type() == PieceType.KING) {
            value = kingEvalBlack[y][x];
        } else if (piece.type() == PieceType.KNIGHT) {
            value = knightEvalBlack[y][x];
        } else if (piece.type() == PieceType.QUEEN) {
            value = queenEvalBlack[y][x];
        } else if (piece.type() == PieceType.ROOK) {
            value = rookEvalBlack[y][x];
        } else if (piece.type() == PieceType.BISHOP) {
            value = bishopEvalBlack[y][x];
        } else {
            value = 0;
        }

        value = value + (piece.type().getValue() * 10);

        return (player.isWhite() && piece.color().isWhite()) || (!player.isWhite() && !piece.color().isWhite()) ? value : -value;
    }

    static final float[][] pawnEvalBlack = {
            {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f},
            {5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f},
            {1.0f, 1.0f, 2.0f, 3.0f, 3.0f, 2.0f, 1.0f, 1.0f},
            {0.5f, 0.5f, 1.0f, 2.5f, 2.5f, 1.0f, 0.5f, 0.5f},
            {0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 0.0f, 0.0f, 0.0f},
            {0.5f, -0.5f, -1.0f, 0.0f, 0.0f, -1.0f, -0.5f, 0.5f},
            {0.5f, 1.0f, 1.0f, -2.0f, -2.0f, 1.0f, 1.0f, 0.5f},
            {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}
    };

    static final float[][] knightEvalBlack = {
            {-5.0f, -4.0f, -3.0f, -3.0f, -3.0f, -3.0f, -4.0f, -5.0f},
            {-4.0f, -2.0f, 0.0f, 0.0f, 0.0f, 0.0f, -2.0f, -4.0f},
            {-3.0f, 0.0f, 1.0f, 1.5f, 1.5f, 1.0f, 0.0f, -3.0f},
            {-3.0f, 0.5f, 1.5f, 2.0f, 2.0f, 1.5f, 0.5f, -3.0f},
            {-3.0f, 0.0f, 1.5f, 2.0f, 2.0f, 1.5f, 0.0f, -3.0f},
            {-3.0f, 0.5f, 1.0f, 1.5f, 1.5f, 1.0f, 0.5f, -3.0f},
            {-4.0f, -2.0f, 0.0f, 0.5f, 0.5f, 0.0f, -2.0f, -4.0f},
            {-5.0f, -4.0f, -3.0f, -3.0f, -3.0f, -3.0f, -4.0f, -5.0f}
    };

    static final float[][] bishopEvalBlack = {
            {-2.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -2.0f},
            {-1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f},
            {-1.0f, 0.0f, 0.5f, 1.0f, 1.0f, 0.5f, 0.0f, -1.0f},
            {-1.0f, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, -1.0f},
            {-1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, -1.0f},
            {-1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f},
            {-1.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f, -1.0f},
            {-2.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -2.0f}
    };

    static final float[][] rookEvalBlack = {
            {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f},
            {0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f},
            {-0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.5f},
            {-0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.5f},
            {-0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.5f},
            {-0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.5f},
            {-0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.5f},
            {0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.0f, 0.0f, 0.0f}
    };

    static final float[][] queenEvalBlack = {
            {-2.0f, -1.0f, -1.0f, -0.5f, -0.5f, -1.0f, -1.0f, -2.0f},
            {-1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f},
            {-1.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f},
            {-0.5f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f, -0.5f},
            {0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f, -0.5f},
            {-1.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f, -1.0f},
            {-1.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f},
            {-2.0f, -1.0f, -1.0f, -0.5f, -0.5f, -1.0f, -1.0f, -2.0f}
    };

    static final float[][] kingEvalBlack = {
            {-3.0f, -4.0f, -4.0f, -5.0f, -5.0f, -4.0f, -4.0f, -3.0f},
            {-3.0f, -4.0f, -4.0f, -5.0f, -5.0f, -4.0f, -4.0f, -3.0f},
            {-3.0f, -4.0f, -4.0f, -5.0f, -5.0f, -4.0f, -4.0f, -3.0f},
            {-3.0f, -4.0f, -4.0f, -5.0f, -5.0f, -4.0f, -4.0f, -3.0f},
            {-2.0f, -3.0f, -3.0f, -4.0f, -4.0f, -3.0f, -3.0f, -2.0f},
            {-1.0f, -2.0f, -2.0f, -2.0f, -2.0f, -2.0f, -2.0f, -1.0f},
            {2.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 2.0f},
            {2.0f, 3.0f, 1.0f, 0.0f, 0.0f, 1.0f, 3.0f, 2.0f}
    };
}
