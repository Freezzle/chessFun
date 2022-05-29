package ch.claudedy.chess.basis;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;

@Accessors(fluent = true)
@Getter
public enum Tile {
    A1('A', '1', 0, 0, Color.BLACK),
    A2('A', '2',0, 1, Color.WHITE),
    A3('A', '3',0, 2, Color.BLACK),
    A4('A', '4',0, 3, Color.WHITE),
    A5('A', '5',0, 4, Color.BLACK),
    A6('A', '6',0, 5, Color.WHITE),
    A7('A', '7',0, 6, Color.BLACK),
    A8('A', '8',0, 7, Color.WHITE),
    B1('B', '1',1, 0, Color.WHITE),
    B2('B', '2',1, 1, Color.BLACK),
    B3('B', '3',1, 2, Color.WHITE),
    B4('B', '4',1, 3, Color.BLACK),
    B5('B', '5',1, 4, Color.WHITE),
    B6('B', '6',1, 5, Color.BLACK),
    B7('B', '7',1, 6, Color.WHITE),
    B8('B', '8',1, 7, Color.BLACK),
    C1('C', '1',2, 0, Color.BLACK),
    C2('C', '2',2, 1, Color.WHITE),
    C3('C', '3',2, 2, Color.BLACK),
    C4('C', '4',2, 3, Color.WHITE),
    C5('C', '5',2, 4, Color.BLACK),
    C6('C', '6',2, 5, Color.WHITE),
    C7('C', '7',2, 6, Color.BLACK),
    C8('C', '8',2, 7, Color.WHITE),
    D1('D', '1',3, 0, Color.WHITE),
    D2('D', '2',3, 1, Color.BLACK),
    D3('D', '3',3, 2, Color.WHITE),
    D4('D', '4',3, 3, Color.BLACK),
    D5('D', '5',3, 4, Color.WHITE),
    D6('D', '6',3, 5, Color.BLACK),
    D7('D', '7',3, 6, Color.WHITE),
    D8('D', '8',3, 7, Color.BLACK),
    E1('E', '1',4, 0, Color.BLACK),
    E2('E', '2',4, 1, Color.WHITE),
    E3('E', '3',4, 2, Color.BLACK),
    E4('E', '4',4, 3, Color.WHITE),
    E5('E', '5',4, 4, Color.BLACK),
    E6('E', '6',4, 5, Color.WHITE),
    E7('E', '7',4, 6, Color.BLACK),
    E8('E', '8',4, 7, Color.WHITE),
    F1('F', '1',5, 0, Color.WHITE),
    F2('F', '2',5, 1, Color.BLACK),
    F3('F', '3',5, 2, Color.WHITE),
    F4('F', '4',5, 3, Color.BLACK),
    F5('F', '5',5, 4, Color.WHITE),
    F6('F', '6',5, 5, Color.BLACK),
    F7('F', '7',5, 6, Color.WHITE),
    F8('F', '8',5, 7, Color.BLACK),
    G1('G', '1',6, 0, Color.BLACK),
    G2('G', '2',6, 1, Color.WHITE),
    G3('G', '3',6, 2, Color.BLACK),
    G4('G', '4',6, 3, Color.WHITE),
    G5('G', '5',6, 4, Color.BLACK),
    G6('G', '6',6, 5, Color.WHITE),
    G7('G', '7',6, 6, Color.BLACK),
    G8('G', '8',6, 7, Color.WHITE),
    H1('H', '1',7, 0, Color.WHITE),
    H2('H', '2',7, 1, Color.BLACK),
    H3('H', '3',7, 2, Color.WHITE),
    H4('H', '4',7, 3, Color.BLACK),
    H5('H', '5',7, 4, Color.WHITE),
    H6('H', '6',7, 5, Color.BLACK),
    H7('H', '7',7, 6, Color.WHITE),
    H8('H', '8',7, 7, Color.BLACK);

    public static final Tile[] VALUES = values();

    private final char col;
    private final char line;
    private final int x;
    private final int y;
    private final Color color;

    Tile(char col, char line, int x, int y, Color color) {
        this.col = col;
        this.line = line;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public static Tile getEnum(int x, int y){
        return Arrays.stream(VALUES).filter(value -> value.x == x && value.y == y).findFirst().orElse(null);
    }
}
