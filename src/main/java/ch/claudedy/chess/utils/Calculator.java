package ch.claudedy.chess.utils;

import ch.claudedy.chess.basis.Board;
import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.Piece;
import ch.claudedy.chess.basis.PieceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Calculator {

    private static final List<Piece> BLACK_PIECES = Arrays.stream("k;q;r;r;b;b;n;n;p;p;p;p;p;p;p;p".split(";")).map(Piece::new).sorted().collect(Collectors.toList());
    private static final List<Piece> WHITE_PIECES = Arrays.stream("k;q;r;r;b;b;n;n;p;p;p;p;p;p;p;p".toUpperCase().split(";")).map(Piece::new).sorted().collect(Collectors.toList());

    public static String giveRemovedPieces(Board board, Color player) {
        List<Piece> alivePieces = board.getAlivePieces(player);
        List<Piece> alivePiecesEnnemy = board.getAlivePieces(player.reverseColor());

        String result = "";

        if (player.isWhite()) {
            List<Piece> clone = new ArrayList<>(WHITE_PIECES);

            for (Piece current : alivePieces) {
                Optional<Piece> found = clone.stream().filter(p -> p.letter().equals(current.letter())).findFirst();
                found.ifPresent(clone::remove);
            }

            result = result + clone.stream().map(Piece::letter).map(Object::toString).collect(Collectors.joining("")).toLowerCase();
        } else {
            List<Piece> clone = new ArrayList<>(BLACK_PIECES);

            for (Piece current : alivePieces) {
                Optional<Piece> found = clone.stream().filter(p -> p.letter().equals(current.letter())).findFirst();
                found.ifPresent(clone::remove);
            }
            result = result + clone.stream().map(Piece::letter).map(Object::toString).collect(Collectors.joining("")).toLowerCase();
        }

        Integer total = alivePieces.stream().map(Piece::type).map(PieceType::value).reduce(0, Integer::sum);
        Integer totalEnnemy = alivePiecesEnnemy.stream().map(Piece::type).map(PieceType::value).reduce(0, Integer::sum);

        long diff = totalEnnemy - total;

        if (diff > 0) {
            result = result + " (+" + diff + ")";
        }

        return result;
    }
}
