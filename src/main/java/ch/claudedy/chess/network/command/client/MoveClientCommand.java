package ch.claudedy.chess.network.command.client;

import ch.claudedy.chess.model.MoveCommand;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class MoveClientCommand implements CommandClient {
    private MoveCommand move;
}
