package ch.claudedy.chess.basis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class PossibleMove implements Serializable {
    private final Tile destination;
    private final MoveType type;
}
