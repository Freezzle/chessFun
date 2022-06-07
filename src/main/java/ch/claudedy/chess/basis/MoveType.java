package ch.claudedy.chess.basis;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
public enum MoveType {
    MOVE, THREAT, THREAT_ENEMY_KING, CASTLE, EN_PASSANT, PROMOTE
}
