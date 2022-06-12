package ch.claudedy.chess.model.enumeration;

import java.io.Serializable;

public enum MoveStatus implements Serializable {
    OK, GAME_ALREADY_OVER, BAD_MOVE_COMMAND, NO_PIECE_SELECTED, PIECE_BLOCKED, ENNEMY_PIECE_SELECTED, CANT_EAT_ENEMY_KING, PIECE_ILLEGAL_MOVE, PIECE_CANT_EAT_ALLY_PIECE, CANT_MOVE_DURING_ANOTHER_MOVE;

    public boolean isOk() {
        return this == OK;
    }
}
