package ch.claudedy.chess.network.command.server;

import ch.claudedy.chess.model.MoveCommand;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class MoveServerCommand implements CommandServer {
    private MoveCommand move;
}
