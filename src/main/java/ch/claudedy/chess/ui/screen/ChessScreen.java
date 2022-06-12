package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.model.Color;
import ch.claudedy.chess.model.MoveCommand;
import ch.claudedy.chess.system.GameType;
import ch.claudedy.chess.ui.listener.MoveDoneListener;
import ch.claudedy.chess.ui.listener.MoveFailedListener;
import ch.claudedy.chess.ui.manager.AIManager;
import ch.claudedy.chess.ui.manager.ChessManager;
import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.manager.NetworkManager;
import ch.claudedy.chess.ui.screen.component.BoardComponentUI;
import ch.claudedy.chess.ui.screen.component.InfoPlayerComponentUI;
import ch.claudedy.chess.ui.screen.model.InfoPlayer;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;

@Accessors(fluent = true)
public class ChessScreen extends JPanel {

    // UI Child
    @Getter
    private BoardComponentUI boardUI;
    private InfoPlayerComponentUI informationWhiteArea;
    private InfoPlayerComponentUI informationBlackArea;
    @Getter
    private InfoPlayer playerWhite;
    @Getter
    private InfoPlayer playerBlack;

    public ChessScreen() {
        setName("CHESS");
        setPreferredSize(new Dimension(600, 700));
        setBounds(new Rectangle(0, 0, 600, 700));

        ChessManager.instance().startNewGame();

        this.initPlayers();
        this.initLayers();
        this.reset();

        this.manageComputerGame();
    }

    private void manageComputerGame() {
        if (GameManager.instance().gameTypeLocal() == GameType.COMPUTER_V_PLAYER) {
            launchComputerMove();
        } else if (GameManager.instance().gameTypeLocal() == GameType.COMPUTER_V_COMPUTER) {
            launchComputerMove();
        }
    }

    public void launchComputerMove() {
        AIManager.instance().computerIsThinking(true);
        new Thread(() -> {
            MoveCommand move = AIManager.getMove();
            boardUI.makeMoveUI(move.startPosition(), move.endPosition(), true);
            AIManager.instance().computerIsThinking(false);
        }).start();
    }

    private void initPlayers() {
        if (GameManager.instance().modeOnline()) {
            InfoPlayer myInfo = NetworkManager.instance().infoPlayer();
            InfoPlayer myOpponentInfo = NetworkManager.instance().infoOpponent();

            if (myInfo.color().isWhite()) {
                playerWhite = myInfo;
                playerBlack = myOpponentInfo;
            } else {
                playerWhite = myOpponentInfo;
                playerBlack = myInfo;
            }
        } else {
            boolean whiteTurn = ChessManager.instance().isWhiteTurn();

            if (GameManager.instance().gameTypeLocal() == GameType.PLAYER_V_PLAYER) {
                playerWhite = new InfoPlayer("Player 1", Color.WHITE, false);
                playerBlack = new InfoPlayer("Player 2", Color.BLACK, false);
            } else if (GameManager.instance().gameTypeLocal() == GameType.PLAYER_V_COMPUTER) {
                if (whiteTurn) {
                    playerWhite = new InfoPlayer("Player", Color.WHITE, false);
                    playerBlack = new InfoPlayer("Computer", Color.BLACK, true);
                } else {
                    playerWhite = new InfoPlayer("Computer", Color.WHITE, true);
                    playerBlack = new InfoPlayer("Player", Color.BLACK, false);
                }
            } else if (GameManager.instance().gameTypeLocal() == GameType.COMPUTER_V_PLAYER) {
                if (whiteTurn) {
                    playerWhite = new InfoPlayer("Computer", Color.WHITE, true);
                    playerBlack = new InfoPlayer("Player", Color.BLACK, false);
                } else {
                    playerWhite = new InfoPlayer("Player", Color.WHITE, false);
                    playerBlack = new InfoPlayer("Computer", Color.BLACK, true);
                }
            } else {
                playerWhite = new InfoPlayer("Computer 1", Color.WHITE, true);
                playerBlack = new InfoPlayer("Computer 2", Color.BLACK, true);
            }
        }
    }

    private void initLayers() {
        if (getComponentCount() != 0) {
            removeAll();
        }

        // View from white SIDE
        if ((!GameManager.instance().modeOnline() && !playerWhite.isComputer()) || (playerWhite.isComputer() && playerBlack.isComputer()) || (GameManager.instance().modeOnline() && NetworkManager.instance().infoPlayer().color().isWhite())) {
            informationBlackArea = new InfoPlayerComponentUI(playerBlack);
            add(informationBlackArea);

            boardUI = new BoardComponentUI(true);
            boardUI.addMoveDoneListener(new MoveDoneListener(this));
            boardUI.addMoveFailedListener(new MoveFailedListener(this));
            add(boardUI);

            informationWhiteArea = new InfoPlayerComponentUI(playerWhite);
            add(informationWhiteArea);
        } else {
            // View from black SIDE
            informationWhiteArea = new InfoPlayerComponentUI(playerWhite);
            add(informationWhiteArea);

            boardUI = new BoardComponentUI(false);
            boardUI.addMoveDoneListener(new MoveDoneListener(this));
            boardUI.addMoveFailedListener(new MoveFailedListener(this));
            add(boardUI);

            informationBlackArea = new InfoPlayerComponentUI(playerBlack);
            add(informationBlackArea);
        }
    }



    public void reset() {
        informationWhiteArea.updateInfoPlayer();
        informationBlackArea.updateInfoPlayer();
        boardUI.resetBoard();
    }
}
