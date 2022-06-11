package ch.claudedy.chess.network.command.server;

import ch.claudedy.chess.ui.screen.model.InfoPlayer;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class StartGameCommand implements CommandServer {
    private InfoPlayer player;
    private InfoPlayer playerOpponent;
}
