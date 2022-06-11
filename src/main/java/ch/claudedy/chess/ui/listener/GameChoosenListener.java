package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.ui.delegate.GameManager;
import ch.claudedy.chess.ui.screen.MainUI;

public class GameChoosenListener {

    private final MainUI main;

    public GameChoosenListener(MainUI main) {
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
