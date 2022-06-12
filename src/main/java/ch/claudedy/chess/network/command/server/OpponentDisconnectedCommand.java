package ch.claudedy.chess.network.command.server;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class OpponentDisconnectedCommand implements CommandServer {
}
