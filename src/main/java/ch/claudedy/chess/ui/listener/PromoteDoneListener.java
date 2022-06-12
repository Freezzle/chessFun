package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.model.enumeration.PieceType;
import ch.claudedy.chess.ui.screen.component.BoardComponentUI;

public class PromoteDoneListener {

    private final BoardComponentUI boardComponentUI;

    public PromoteDoneListener(BoardComponentUI boardComponentUI) {
        this.boardComponentUI = boardComponentUI;
    }

    public void onPromoteDoneListener(PieceType pieceType) {
        boardComponentUI.promoteDone(pieceType.abrevTechnicalBlack());
    }
}
