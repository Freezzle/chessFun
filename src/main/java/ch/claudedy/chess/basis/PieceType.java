package ch.claudedy.chess.basis;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PieceType {
    ROOK("r", 'R', 'r', 5),
    KNIGHT("n", 'N', 'n', 3),
    BISHOP("b", 'B', 'b', 3),
    QUEEN("q", 'Q', 'q', 9),
    KING("k", 'K', 'k', 2),
    PAWN("", 'P', 'p', 1);

    public static final PieceType[] VALUES = values();

    private final String abrev;
    private final char abrevTechnicalWhite;
    private final char abrevTechnicalBlack;
    private final int value;

    PieceType(final String abrev, final char abrevTechnicalWhite, final char abrevTechnicalBlack, int value) {
        this.abrev = abrev;
        this.abrevTechnicalWhite = abrevTechnicalWhite;
        this.abrevTechnicalBlack = abrevTechnicalBlack;
        this.value = value;
    }

    public static PieceType getFromAbrevTechnical(char character) {
        if (Character.isUpperCase(character)) {
            return Arrays.stream(PieceType.VALUES)
                    .filter(value -> value.getAbrevTechnicalWhite() == character)
                    .findFirst()
                    .orElse(null);
        }

        return Arrays.stream(PieceType.VALUES)
                .filter(value -> value.getAbrevTechnicalBlack() == character)
                .findFirst()
                .orElse(null);
    }

    public static Color getColorFromCharacter(char character) {
        if (Character.isUpperCase(character)) {
            return Color.WHITE;
        }

        return Color.BLACK;
    }

    public boolean canMoveDiagonally() { return this == QUEEN || this == BISHOP; }
    public boolean canMoveLinearly() {
        return this == QUEEN || this == ROOK;
    }
    public boolean canMoveInLShape() {
        return this == KNIGHT;
    }
    public boolean canOnlyMoveOneCase() {
        return this == KING;
    }
    public boolean canMoveOnlyForward() {
        return this == PAWN;
    }
}
