package ch.claudedy.chess.ui.delegate;

import ch.claudedy.chess.network.ChessClient;
import ch.claudedy.chess.ui.InfoPlayer;
import ch.claudedy.chess.ui.screen.ChessUI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.IOException;

@Accessors(fluent = true)
public class NetworkDelegate {

    private static NetworkDelegate instance;

    @Getter
    private ChessClient client;
    private boolean gameStarted = false;
    @Setter
    @Getter
    private InfoPlayer infoPlayer;
    @Setter
    @Getter
    private InfoPlayer infoOpponent;

    @Setter
    @Getter
    private ChessUI game;

    private NetworkDelegate() {
    }

    public void startConnection() throws IOException {
        if (instance.client == null) {
            instance.client = new ChessClient();
        }
    }

    public boolean isModeOnline() {
        return instance.client != null;
    }

    public void setGameStarted(boolean gameStarted) {
        instance.gameStarted = gameStarted;
    }

    public boolean hasGameStarted() {
        return instance.gameStarted;
    }

    public static NetworkDelegate getInstance() {
        if (instance == null) {
            instance = new NetworkDelegate();
        }

        return instance;
    }
}
