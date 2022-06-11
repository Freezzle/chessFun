package ch.claudedy.chess.network;

import ch.claudedy.chess.ApplicationServerOnline;
import ch.claudedy.chess.basis.Color;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class GameInfo {
    private ApplicationServerOnline.ServiceThread whitePlayer;
    private ApplicationServerOnline.ServiceThread blackPlayer;

    public ApplicationServerOnline.ServiceThread getOpponent(Color colorPlayer) {
        return colorPlayer.isWhite() ? blackPlayer : whitePlayer;
    }
}
