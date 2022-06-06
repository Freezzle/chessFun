package ch.claudedy.chess.basis;

public enum GameStatus {
    WAITING_MOVE, EXECUTING, CHECKMATED, STALEMATED, RULES_50, IMPOSSIBILITY_CHECKMATE, REPETITION_RULE;

    public boolean isGameOver() {
        return this == CHECKMATED || this == STALEMATED || this == RULES_50 || this == IMPOSSIBILITY_CHECKMATE || this == REPETITION_RULE;
    }

    public boolean isGameWaitingMove() {
        return this == WAITING_MOVE;
    }
}
