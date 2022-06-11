package ch.claudedy.chess.network;

import ch.claudedy.chess.ui.InfoPlayer;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class SearchingGameCommand implements CommandClient {
    private InfoPlayer player;
}
