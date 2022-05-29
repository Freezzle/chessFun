package ch.claudedy.chess.basis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import ch.claudedy.chess.basis.Tile;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class MoveCommand {
    private final Tile startPosition;
    private final Tile endPosition;
}
