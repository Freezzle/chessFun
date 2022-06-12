package ch.claudedy.chess.model;

import ch.claudedy.chess.model.enumeration.MoveType;
import ch.claudedy.chess.model.enumeration.Tile;
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
