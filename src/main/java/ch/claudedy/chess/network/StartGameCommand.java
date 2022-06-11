package ch.claudedy.chess.network;

import ch.claudedy.chess.ui.InfoPlayer;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class StartGameCommand implements CommandServer {
    private InfoPlayer player;
    private InfoPlayer playerOpponent;
}