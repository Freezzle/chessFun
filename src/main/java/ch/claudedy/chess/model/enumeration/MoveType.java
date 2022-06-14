package ch.claudedy.chess.model.enumeration;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(fluent = true)
@Getter
public enum MoveType implements Serializable {
    PAWN_MOVE, MOVE_WITHOUT_CAPTURING, MOVE_WITH_CAPTURING, ONLY_THREAT, ALLY_PROTECTION, THREAT_ENEMY_KING, CASTLE, EN_PASSANT, PROMOTE
}
