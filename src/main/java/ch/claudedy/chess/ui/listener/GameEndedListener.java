package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.manager.NetworkManager;
import ch.claudedy.chess.ui.screen.AppScreen;

public class GameEndedListener {

    private final AppScreen main;

    public GameEndedListener(AppScreen main) {
        this.main = main;
    }

    public void onGameEndedListener() {
        if (GameManager.instance().modeOnline()) {
            NetworkManager.instance().client(null);
            NetworkManager.instance().infoPlayer(null);
            NetworkManager.instance().infoOpponent(null);
            NetworkManager.instance().game(null);
        }

        GameManager.instance().modeOnline(false);
        GameManager.instance().setGameStarted(false);
        GameManager.instance().chess(null);

        this.main.comeBackMenu();
    }
}
