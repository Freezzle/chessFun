package ch.claudedy.chess.network.command.server;

import ch.claudedy.chess.model.enumeration.GameStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class GameEndedCommand implements CommandServer {
    private GameStatus status;
}
