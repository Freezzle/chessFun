package ch.claudedy.chess.utils;

import ch.claudedy.chess.basis.Board;
import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.Square;
import ch.claudedy.chess.basis.Tile;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Setter
@Getter
public class FenUtils {

    public static String boardToFen(Board board) {
        // rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR
        StringBuilder fen = new StringBuilder();
        Square[][] squares = board.squares();
        for (int y = 7; y >= 0; y--) {
            int incCount = 0;

            for (int x = 0; x <= 7; x++) {
                if (squares[x][y].piece() == null) {
                    incCount++;
                } else {
                    if (incCount != 0) {
                        fen.append(incCount);
                        incCount = 0;
                    }
                    fen.append(squares[x][y].piece().letter());
                }

                if (x == 7 && incCount != 0) {
                    fen.append(incCount);
                }
            }

            if (y != 0) {
                fen.append("/");
            }
        }
        fen.append(" ");


        // b | w
        if (board.isWhiteCurrentPlayer()) {
            fen.append("w");
        } else {
            fen.append("b");
        }
        fen.append(" ");


        // KQkq | -
        String roques = "";
        if (board.canwKRoque()) {
            roques += "K";
        }
        if (board.canwQRoque()) {
            roques += "Q";
        }
        if (board.canbkRoque()) {
            roques += "k";
        }
        if (board.canbqRoque()) {
            roques += "q";
        }

        if ("".equals(roques)) {
            fen.append("-");
        } else {
            fen.append(roques);
        }
        fen.append(" ");

        // e3 | -
        if (board.enPassant() == null) {
            fen.append("-");
        } else {
            fen.append(board.enPassant().name().toLowerCase(Locale.ROOT));
        }
        fen.append(" ");

        // Number
        fen.append(board.fiftyRules());
        fen.append(" ");

        // Number
        fen.append(board.moves());
        return fen.toString();
    }

    public static Board fenToBoard(String fullFen) {
        Board board = new Board();

        String[] spaceBreaks = fullFen.split(" ");

        managePieces(spaceBreaks[0], board);
        managePlayerToMove(spaceBreaks[1], board);
        manageRoques(spaceBreaks[2], board);
        manageEnPassant(spaceBreaks[3], board);
        manageFiftyRule(spaceBreaks[4], board);
        manageMoves(spaceBreaks[5], board);

        // Last move to add

        return board;
    }

    private static void manageMoves(String moves, Board board) {
        board.moves(Integer.parseInt(moves));
    }

    private static void manageFiftyRule(String fiftyRules, Board board) {
        board.fiftyRules(Integer.parseInt(fiftyRules));
    }

    private static void manageEnPassant(String enPassant, Board board) {
        if (enPassant.equals("-"))
            return;

        board.enPassant(Tile.valueOf(enPassant.toUpperCase(Locale.ROOT)));
    }

    private static void managePieces(String positions, Board board) {
        String[] piecesPositions = positions.split("/");

        for (int i = 0; i < piecesPositions.length; i++) {
            int yCurrent = 7 - i;
            char[] chars = piecesPositions[i].toCharArray();

            int xCurrent = 0;
            for (char currentChar : chars) {
                if (Character.isDigit(currentChar)) {
                    int digit = Character.getNumericValue(currentChar);
                    xCurrent += digit;
                } else {
                    board.getSquare(Tile.getEnum(xCurrent, yCurrent)).placePiece(currentChar);
                    xCurrent += 1;
                }
            }
        }
    }

    private static void managePlayerToMove(String player, Board board) {
        board.currentPlayer(player.equals("b") ? Color.BLACK : Color.WHITE);
    }

    private static void manageRoques(String roques, Board board) {
        char[] roquesPossibles = roques.toCharArray();
        for (char roque : roquesPossibles) {
            if (roque == '-')
                break;

            if (roque == 'K')
                board.canwKRoque(true);

            if (roque == 'Q')
                board.canwQRoque(true);

            if (roque == 'k')
                board.canbkRoque(true);

            if (roque == 'q')
                board.canbqRoque(true);
        }
    }
}
