package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.screen.AppScreen;

public class GameChoosenListener {

    private final AppScreen main;

    public GameChoosenListener(AppScreen main) {
        this.main = main;
    }

    public void onGameLocalChoosenListener() {
        this.main.startChoosingLocalGame();
    }

    public void launchGameListener() {
        if (GameManager.instance().modeOnline()) {
            this.main.startWaitingGameOnline();
            this.main.startGameOnline();
        } else {
            this.main.startGameLocal();
        }
    }
}
