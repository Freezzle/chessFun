package ch.claudedy.chess.network.command.client;

import ch.claudedy.chess.ui.screen.model.InfoPlayer;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class SearchingGameCommand implements CommandClient {
    private InfoPlayer infoPlayer;
}
