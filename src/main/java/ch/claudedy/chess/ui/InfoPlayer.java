package ch.claudedy.chess.ui;

import ch.claudedy.chess.basis.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;


@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class InfoPlayer {
    private final String name;
    private final String elo;
    private final Color color;
    private final boolean isComputer;
}
