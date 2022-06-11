package ch.claudedy.chess.ui.delegate;

import ch.claudedy.chess.network.ChessClient;
import ch.claudedy.chess.ui.InfoPlayer;
import ch.claudedy.chess.ui.screen.ChessUI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.IOException;

@Accessors(fluent = true)
public class NetworkManager {

    private static NetworkManager instance;

    @Getter
    private ChessClient client;

    @Setter
    @Getter
    private InfoPlayer infoPlayer;
    @Setter
    @Getter
    private InfoPlayer infoOpponent;

    @Setter
    @Getter
    private ChessUI game;

    private NetworkManager() {
    }

    public static NetworkManager instance() {
        if (instance == null) {
            instance = new NetworkManager();
        }

        return instance;
    }

    public void startConnection() throws IOException {
        if (instance.client == null) {
            instance.client = new ChessClient();
        }
    }
}
