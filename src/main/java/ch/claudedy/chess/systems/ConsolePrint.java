package ch.claudedy.chess.systems;

import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.utils.Calculator;
import ch.claudedy.chess.utils.FenUtils;

public class ConsolePrint {

    public static final String RESET = "\033[0m";

    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String RED_BOLD = "\033[1;91m";
    public static final String GREEN_BOLD = "\033[1;92m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static void execute(Chess chess) {
        if (!SystemConfig.PRINT_CONSOLE) {
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
                    if (chess.currentBoard().isWhiteCurrentPlayer()) {
                        System.out.print(ANSI_RED_BACKGROUND);
                    } else {
                        System.out.print(ANSI_GREEN_BACKGROUND);
                    }

                    if (piece != null) {
                        System.out.print(BLACK_BOLD);
                    }
                } else if (endMove != null && endMove.equals(square.tile())) {
                    if (chess.currentBoard().isWhiteCurrentPlayer()) {
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
        System.out.print(FenUtils.boardToFen(chess.currentBoard()));
        System.out.print(chess.actualMove() != null ? ";" + chess.actualMove().convert() : ";-");
        System.out.println();

        System.out.println("Status : " + chess.gameStatus());
        System.out.println();
        System.out.println();
    }
}
