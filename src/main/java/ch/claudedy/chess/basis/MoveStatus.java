package ch.claudedy.chess.basis;

public enum MoveStatus {
    OK, BAD_SELECTION, NO_PIECE_SELECTED, PIECE_BLOCKED, ENNEMY_PIECE_SELECTED, CANT_EAT_ENEMY_KING, PIECE_ILLEGAL_MOVE, PIECE_CANT_EAT_ALLY_PIECE, CANT_MOVE_DURING_ANOTHER_MOVE;

    public boolean isStatusError() {
        return !isStatusOk();
    }

    public boolean isStatusOk() {
        return this == OK;
    }
}
