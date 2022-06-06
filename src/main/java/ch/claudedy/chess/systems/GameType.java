package ch.claudedy.chess.systems;

public enum GameType {
    PLAYER_V_PLAYER, PLAYER_V_COMPUTER, COMPUTER_V_PLAYER, COMPUTER_V_COMPUTER;

    public boolean containsInLessAComputer() {
        return this == PLAYER_V_COMPUTER || this == COMPUTER_V_COMPUTER || this == COMPUTER_V_PLAYER;
    }
}
