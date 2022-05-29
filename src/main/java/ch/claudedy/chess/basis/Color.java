package ch.claudedy.chess.basis;

public enum Color {
    WHITE,
    BLACK;

    public boolean isSameColor(Color color) {
        return this == color;
    }

    public boolean isWhite() {
        return this == WHITE;
    }
}
