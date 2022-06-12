package ch.claudedy.chess.system;

import ch.claudedy.chess.model.Chess;
import ch.claudedy.chess.model.Piece;
import ch.claudedy.chess.model.Square;
import ch.claudedy.chess.model.enumeration.Color;
import ch.claudedy.chess.model.enumeration.Tile;
import ch.claudedy.chess.util.Calculator;
import ch.claudedy.chess.util.FenConverter;

public class ConsolePrint {

    private static final String RESET = "\033[0m";

    private static final String BLACK_BOLD = "\033[1;30m";
    private static final String RED_BOLD = "\033[1;91m";
    private static final String GREEN_BOLD = "\033[1;92m";

    private static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static void execute(Chess chess) {
        if (!SystemSettings.PRINT_CONSOLE) {
            return;
        }

        Tile startMove = chess.actualMove() != null ? chess.actualMove().startPosition() : null;
        Tile endMove = chess.actualMove() != null ? chess.actualMove().endPosition() : null;

        System.out.println("Black : " + Calculator.giveRemovedPieces(chess.currentBoard(), Color.WHITE));

        System.out.print("  ");
        for (int x = 0; x <= 7; x++) {
            System.out.print(" " + Tile.getEnum(x, 7).col() + " ");
        }
        System.out.println();

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                Square square = chess.currentBoard().squares()[x][y];
                Piece piece = square.piece();

                if (x == 0) {
                    System.out.print(Tile.getEnum(x, y).line() + " ");
                }

                if (startMove != null && startMove.equals(square.tile())) {
                    if (chess.currentBoard().isWhiteTurn()) {
                        System.out.print(ANSI_RED_BACKGROUND);
                    } else {
                        System.out.print(ANSI_GREEN_BACKGROUND);
                    }

                    if (piece != null) {
                        System.out.print(BLACK_BOLD);
                    }
                } else if (endMove != null && endMove.equals(square.tile())) {
                    if (chess.currentBoard().isWhiteTurn()) {
                        System.out.print(ANSI_RED_BACKGROUND);
                    } else {
                        System.out.print(ANSI_GREEN_BACKGROUND);
                    }

                    if (piece != null) {
                        System.out.print(BLACK_BOLD);
                    }
                } else {
                    if (square.tile().isWhiteTile()) {
                        System.out.print(ANSI_WHITE_BACKGROUND);
                    } else {
                        System.out.print(ANSI_BLACK_BACKGROUND);
                    }

                    if (piece != null && piece.isWhitePiece()) {
                        System.out.print(GREEN_BOLD);
                    } else if (piece != null && !piece.isWhitePiece()) {
                        System.out.print(RED_BOLD);
                    }
                }

                System.out.print(" ");
                System.out.print(chess.currentBoard().squares()[x][y].printSquare());
                System.out.print(" ");
                System.out.print(RESET);

                if (x == 7) {
                    System.out.print(" " + Tile.getEnum(x, y).line() + " ");
                }
            }
            System.out.println();
        }

        System.out.print("  ");
        for (int x = 0; x <= 7; x++) {
            System.out.print(" " + Tile.getEnum(x, 0).col() + " ");
        }
        System.out.println();
        System.out.println("White : " + Calculator.giveRemovedPieces(chess.currentBoard(), Color.BLACK));

        System.out.println();
        System.out.print(FenConverter.boardToFen(chess.currentBoard()));
        System.out.print(chess.actualMove() != null ? ";" + chess.actualMove().convert() : ";-");
        System.out.println();

        System.out.println("Status : " + chess.gameStatus());
        System.out.println();
    }
}
