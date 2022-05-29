package model.chess.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import model.chess.basis.Tile;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class MoveCommand {
    private final Tile startPosition;
    private final Tile endPosition;
}
