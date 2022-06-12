package ch.claudedy.chess.network.command.client;

import ch.claudedy.chess.model.enumeration.GameStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class GameEndedClientCommand implements CommandClient {
    private GameStatus status;
}
