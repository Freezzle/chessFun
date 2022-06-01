package ch.claudedy.chess.basis;

public enum Color {
    WHITE,
    BLACK;

    public boolean isSameColor(Color color) {
        return this == color;
    }

    public Color reverseColor() {
        return WHITE == this ? BLACK : WHITE;
    }

    public boolean isWhite() {
        return this == WHITE;
    }
}
