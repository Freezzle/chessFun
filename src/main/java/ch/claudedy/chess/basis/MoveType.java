package ch.claudedy.chess.basis;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(fluent = true)
@Getter
public enum MoveType implements Serializable {
    MOVE, THREAT, THREAT_ENEMY_KING, CASTLE, EN_PASSANT, PROMOTE
}
