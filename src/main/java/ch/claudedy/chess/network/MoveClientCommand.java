package ch.claudedy.chess.network;

import ch.claudedy.chess.basis.MoveCommand;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class MoveClientCommand implements CommandClient {
    private MoveCommand move;
}
