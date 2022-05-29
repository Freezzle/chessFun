package ch.claudedy.chess.systems;

import ch.claudedy.chess.basis.Chess;
import ch.claudedy.chess.basis.Piece;
import ch.claudedy.chess.basis.Square;
import ch.claudedy.chess.basis.Tile;
import ch.claudedy.chess.utils.FenUtils;

import java.text.DecimalFormat;

public class ConsolePrint {
    private static final DecimalFormat df = new DecimalFormat("00.00");

    public static final String RESET = "\033[0m";

    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String RED_BOLD = "\033[1;91m";
    public static final String GREEN_BOLD = "\033[1;92m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static void execute(Chess chess) {
        if (!SystemConfig.PRINT_CONSOLE) {
            return;
        }

        Tile startMove = chess.actualMove() != null ? chess.actualMove().startPosition() : null;
        Tile endMove = chess.actualMove() != null ? chess.actualMove().endPosition() : null;

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                Square square = chess.currentBoard().squares()[x][y];
                Piece piece = square.piece();

                if (startMove != null && startMove.equals(square.tile())) {
                    if (chess.currentBoard().currentPlayer().isWhite()) {
                        System.out.print(ANSI_RED_BACKGROUND);
                    } else {
                        System.out.print(ANSI_GREEN_BACKGROUND);
                    }

                    if (piece != null) {
                        System.out.print(BLACK_BOLD);
                    }
                } else if (endMove != null && endMove.equals(square.tile())) {
                    if (chess.currentBoard().currentPlayer().isWhite()) {
                        System.out.print(ANSI_RED_BACKGROUND);
                    } else {
                        System.out.print(ANSI_GREEN_BACKGROUND);
                    }

                    if (piece != null) {
                        System.out.print(BLACK_BOLD);
                    }
                } else {
                    if (square.tile().color().isWhite()) {
                        System.out.print(ANSI_WHITE_BACKGROUND);
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND);
                    }

                    if (piece != null && piece.color().isWhite()) {
                        System.out.print(GREEN_BOLD);
                    } else if (piece != null && !piece.color().isWhite()) {
                        System.out.print(RED_BOLD);
                    }
                }

                System.out.print(" ");
                System.out.print(chess.currentBoard().squares()[x][y].printSquare());
                System.out.print(" ");
                System.out.print(RESET);
            }
            System.out.println();
        }
        System.out.println(FenUtils.boardToFen(chess.currentBoard()));
        System.out.println();
    }
}
