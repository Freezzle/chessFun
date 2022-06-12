package ch.claudedy.chess.model;

import ch.claudedy.chess.model.enumeration.Tile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class MoveCommand implements Serializable {
    private final Tile startPosition;
    private final Tile endPosition;
    private final Character promote;

    public static MoveCommand convert(String move) {
        if (move == null || move.length() < 4) {
            return new MoveCommand(null, null, null);
        }

        if (move.length() == 5) {
            return new MoveCommand(Tile.valueOf(move.substring(0, 2).toUpperCase()), Tile.valueOf(move.substring(2, 4).toUpperCase()), move.substring(4, 5).toCharArray()[0]);
        }

        return new MoveCommand(Tile.valueOf(move.substring(0, 2).toUpperCase()), Tile.valueOf(move.substring(2, 4).toUpperCase()), null);
    }

    public String convert() {
        return startPosition.name() + endPosition.name() + ((promote != null) ? promote : "");
    }
}
