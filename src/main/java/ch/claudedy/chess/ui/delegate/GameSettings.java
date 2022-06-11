package ch.claudedy.chess.ui.delegate;

import ch.claudedy.chess.systems.GameType;
import ch.claudedy.chess.systems.SystemConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class GameSettings {

    private static GameSettings instance;

    @Getter
    @Setter
    private GameType gameType = SystemConfig.GAME_TYPE;

    @Getter
    @Setter
    private boolean isPlayerOneComputer = SystemConfig.GAME_TYPE == GameType.COMPUTER_V_COMPUTER || SystemConfig.GAME_TYPE == GameType.COMPUTER_V_PLAYER;

    @Getter
    @Setter
    private boolean isPlayerTwoComputer = SystemConfig.GAME_TYPE == GameType.COMPUTER_V_COMPUTER || SystemConfig.GAME_TYPE == GameType.PLAYER_V_COMPUTER;

    @Getter
    @Setter
    private boolean launchOnline = true;


    private GameSettings() {
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }

        return instance;
    }
}
