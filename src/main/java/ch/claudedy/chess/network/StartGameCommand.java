package ch.claudedy.chess.network;

import ch.claudedy.chess.basis.Color;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class StartGameCommand implements CommandServer {
    private Color colorPlayer;
}
