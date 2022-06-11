package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.screen.AppScreen;

public class GameChoosenListener {

    private final AppScreen main;

    public GameChoosenListener(AppScreen main) {
        this.main = main;
    }

    public void onGameChoosenListener() {
        if (GameManager.instance().modeOnline()) {
            this.main.printWaitingPlayer();
            this.main.initGameOnline();
        } else {
            this.main.initGameLocal();
        }
    }
}
