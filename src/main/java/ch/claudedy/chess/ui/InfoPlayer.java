package ch.claudedy.chess.ui;

import ch.claudedy.chess.basis.Color;
import lombok.Getter;

@Getter
public class InfoPlayer {
    private final String name;
    private final String elo;
    private final Color color;
    private final boolean isComputer;

    public InfoPlayer(String name, String elo, Color color, boolean isComputer) {
        this.name = name;
        this.elo = elo;
        this.color = color;
        this.isComputer = isComputer;
    }
}
