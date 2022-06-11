package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.model.Board;
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
import ch.claudedy.chess.ui.screen.model.InfoPlayer;
import ch.claudedy.chess.ui.screen.util.UIComponentFactory;
import ch.claudedy.chess.util.Calculator;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;

@Accessors(fluent = true)
public class ChessScreen extends JPanel {

    // UI Child
    @Getter
    private BoardComponentUI boardUI;
    private JPanel informationWhiteArea;
    private JPanel informationBlackArea;
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
            informationBlackArea = UIComponentFactory.createPanel("INFORMATION_BLACK", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 0, 600, 50));
            add(informationBlackArea);

            boardUI = new BoardComponentUI(true);
            boardUI.addMoveDoneListener(new MoveDoneListener(this));
            boardUI.addMoveFailedListener(new MoveFailedListener(this));
            add(boardUI);

            informationWhiteArea = UIComponentFactory.createPanel("INFORMATION_WHITE", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 650, 600, 50));
            add(informationWhiteArea);
        } else {
            // View from black SIDE
            informationWhiteArea = UIComponentFactory.createPanel("INFORMATION_WHITE", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 0, 600, 50));
            add(informationWhiteArea);

            boardUI = new BoardComponentUI(false);
            boardUI.addMoveDoneListener(new MoveDoneListener(this));
            boardUI.addMoveFailedListener(new MoveFailedListener(this));
            add(boardUI);

            informationBlackArea = UIComponentFactory.createPanel("INFORMATION_BLACK", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 650, 600, 50));
            add(informationBlackArea);
        }
    }

    private void updateInformationArea() {
        if (informationWhiteArea.getComponentCount() != 0) {
            informationWhiteArea.removeAll();
        }
        if (informationBlackArea.getComponentCount() != 0) {
            informationBlackArea.removeAll();
        }

        Board currentBoard = ChessManager.instance().currentBoard();

        UIComponentFactory.createTextField(informationWhiteArea, "WHITE_PLAYER", playerWhite.name() + (currentBoard.isWhiteTurn() ? " - your turn" : ""), java.awt.Color.WHITE, java.awt.Color.BLACK);
        UIComponentFactory.createTextField(informationWhiteArea, "ENNEMY_BLACK_PIECES_REMOVED", Calculator.giveRemovedPieces(currentBoard, Color.BLACK), java.awt.Color.WHITE, java.awt.Color.BLACK);
        informationWhiteArea.doLayout();

        UIComponentFactory.createTextField(informationBlackArea, "BLACK_PLAYER", playerBlack.name() + (!currentBoard.isWhiteTurn() ? " - your turn" : ""), java.awt.Color.DARK_GRAY, java.awt.Color.WHITE);
        UIComponentFactory.createTextField(informationBlackArea, "ENNEMY_WHITE_PIECES_REMOVED", Calculator.giveRemovedPieces(currentBoard, Color.WHITE), java.awt.Color.DARK_GRAY, java.awt.Color.WHITE);
        informationBlackArea.doLayout();
    }

    public void reset() {
        this.updateInformationArea();
        boardUI.resetBoard();
    }
}
