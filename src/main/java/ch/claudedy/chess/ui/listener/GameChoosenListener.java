package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.ui.screen.MainUI;

public class GameChoosenListener {

    private final MainUI main;

    public GameChoosenListener(MainUI main) {
        this.main = main;
    }

    public void onGameChoosenListener() {
        this.main.printWaitingPlayer();
        this.main.initGame();
    }
}
