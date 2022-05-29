package model.chess.basis;

public enum Color {
    WHITE,
    BLACK;

    public static Color reverse(Color color){
        return WHITE.isSameColor(color) ? BLACK : WHITE;
    }

    public boolean isSameColor(Color color) {
        return this == color;
    }

    public boolean isWhite() {
        return this == WHITE;
    }
}
