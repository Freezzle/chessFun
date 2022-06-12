package ch.claudedy.chess;

import ch.claudedy.chess.model.Chess;
import ch.claudedy.chess.model.MoveCommand;
import ch.claudedy.chess.model.PossibleMove;
import ch.claudedy.chess.model.Square;
import ch.claudedy.chess.util.FenConverter;

import java.io.IOException;
import java.util.List;

public class ApplicationTest {
    private static int counter = 0;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        Chess chess = new Chess("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", null);

        deep(chess, 4);
        System.out.println("Time : " + Long.valueOf(System.currentTimeMillis() - startTime).toString() + "ms for number moves : " + counter);
    }

    private static void deep(Chess chess, Integer deep) {
        if (deep == 0) {
            return;
        }
        List<Square> alivePieces = chess.currentBoard().getSquaresAlivePieces(chess.currentBoard().currentPlayer());

        deep--;
        for (Square square : alivePieces) {
            List<PossibleMove> legalMoves = chess.getLegalMoves(square.tile());
            if (deep == 0) {
                counter += legalMoves.size();
            }

            for (PossibleMove move : legalMoves) {
                Chess chessFake = new Chess(FenConverter.boardToFen(chess.currentBoard()), chess.actualMove());
                chessFake.makeMove(new MoveCommand(square.tile(), move.destination(), null));

                deep(chessFake, deep);
            }
        }
    }
}
