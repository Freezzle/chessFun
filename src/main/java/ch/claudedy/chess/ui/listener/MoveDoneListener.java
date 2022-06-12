package ch.claudedy.chess.ui.listener;

import ch.claudedy.chess.model.MoveCommand;
import ch.claudedy.chess.network.command.client.GameEndedClientCommand;
import ch.claudedy.chess.network.command.client.MoveClientCommand;
import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.manager.NetworkManager;
import ch.claudedy.chess.ui.screen.ChessScreen;

public class MoveDoneListener {

    private final ChessScreen chessScreen;

    public MoveDoneListener(ChessScreen chessScreen) {
        this.chessScreen = chessScreen;
    }

    public void onMoveDoneListener(MoveCommand move, boolean fromLocalCommand) {
        this.chessScreen.reset();

        if (GameManager.instance().modeOnline() && fromLocalCommand) {
            NetworkManager.instance().client().send(new MoveClientCommand().move(move));

            if (GameManager.instance().chess().gameStatus().isGameOver()) {
                NetworkManager.instance().client().send(new GameEndedClientCommand().status(GameManager.instance().chess().gameStatus()));
            }
        }

        if (GameManager.instance().chess().gameStatus().isGameOver()) {
            return;
        }

        if (this.chessScreen.playerWhite().isComputer() && GameManager.instance().isWhiteTurn() || this.chessScreen.playerBlack().isComputer() && !GameManager.instance().isWhiteTurn()) {
            this.chessScreen.launchComputerMove();
        }
    }
}
