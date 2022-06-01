package ch.claudedy.chess.basis;

public enum MoveFeedBack {
    RUNNING, BAD_SELECTION, NO_PIECE_SELECTED, PIECE_BLOCKED, ENNEMY_PIECE_SELECTED, CANT_EAT_ENEMY_KING, PIECE_ILLEGAL_MOVE, PIECE_CANT_EAT_ALLY_PIECE, CHECKMATED, STALEMATED, RULES_50;

    public boolean isGameOver() {
        return this == CHECKMATED || this == STALEMATED || this == RULES_50;
    }

    public boolean isStatusError() {
        return this != RUNNING && !isGameOver();
    }

    public boolean isStatusOk() {
        return this == RUNNING;
    }
}
