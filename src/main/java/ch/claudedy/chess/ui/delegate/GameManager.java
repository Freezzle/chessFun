package ch.claudedy.chess.ui.delegate;

import ch.claudedy.chess.systems.GameType;
import ch.claudedy.chess.systems.SystemConfig;
import ch.claudedy.chess.ui.InfoPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class GameManager {

    private static GameManager instance;

    @Getter
    @Setter
    private GameType gameTypeLocal = SystemConfig.GAME_TYPE;

    @Getter
    @Setter
    private boolean modeOnline = false;

    @Getter
    @Setter
    private boolean isPlayerOneComputer = gameTypeLocal == GameType.COMPUTER_V_COMPUTER || gameTypeLocal == GameType.COMPUTER_V_PLAYER;

    @Getter
    @Setter
    private boolean isPlayerTwoComputer = gameTypeLocal == GameType.COMPUTER_V_COMPUTER || gameTypeLocal == GameType.PLAYER_V_COMPUTER;

    @Getter
    @Setter
    private InfoPlayer player = new InfoPlayer().name("Name").isComputer(false);

    private boolean gameStarted = false;

    private GameManager() {
    }

    public static GameManager instance() {
        if (instance == null) {
            instance = new GameManager();
        }

        return instance;
    }

    public void setGameStarted(boolean gameStarted) {
        instance.gameStarted = gameStarted;
    }

    public boolean hasGameStarted() {
        return instance.gameStarted;
    }
}
