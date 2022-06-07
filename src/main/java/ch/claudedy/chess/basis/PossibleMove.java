package ch.claudedy.chess.basis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class PossibleMove {
    private final Tile destination;
    private final MoveType type;
}
