package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.ui.delegate.NetworkDelegate;
import ch.claudedy.chess.ui.screen.MainUI;

public class GameChoosenListener {

    private final MainUI main;

    public GameChoosenListener(MainUI main) {
        this.main = main;
    }

    public void onGameChoosenListener() {

        if (NetworkDelegate.getInstance().isModeOnline()) {
            this.main.printWaitingPlayer();
        }

        this.main.initGame();
    }
}
