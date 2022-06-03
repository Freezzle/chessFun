package ch.claudedy.chess.basis;

public enum GameStatus {
    WAITING_MOVE, EXECUTING, CHECKMATED, STALEMATED, RULES_50;

    public boolean isGameOver() {
        return this == CHECKMATED || this == STALEMATED || this == RULES_50;
    }
    public boolean isGameWaitingMove() {
        return this == WAITING_MOVE;
    }
}
