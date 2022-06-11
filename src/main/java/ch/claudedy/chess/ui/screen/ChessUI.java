package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.basis.Board;
import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.MoveCommand;
import ch.claudedy.chess.systems.GameType;
import ch.claudedy.chess.ui.InfoPlayer;
import ch.claudedy.chess.ui.UIFactory;
import ch.claudedy.chess.ui.delegate.AIDelegate;
import ch.claudedy.chess.ui.delegate.ChessDelegate;
import ch.claudedy.chess.ui.delegate.GameSettings;
import ch.claudedy.chess.ui.delegate.NetworkDelegate;
import ch.claudedy.chess.ui.listener.MoveDoneListener;
import ch.claudedy.chess.ui.listener.MoveFailedListener;
import ch.claudedy.chess.utils.Calculator;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;

@Accessors(fluent = true)
public class ChessUI extends JPanel {

    // UI Child
    @Getter
    private BoardUI boardUI;
    private JPanel informationWhiteArea;
    private JPanel informationBlackArea;
    @Getter
    private InfoPlayer playerWhite;
    @Getter
    private InfoPlayer playerBlack;

    public ChessUI() {
        setName("CHESS");
        setPreferredSize(new Dimension(600, 700));
        setBounds(new Rectangle(0, 0, 600, 700));

        ChessDelegate.startNewGame();

        this.initPlayers();
        this.initLayers();
        this.reset();

        this.manageComputerGame();
    }

    private void manageComputerGame() {
        if (GameSettings.getInstance().gameType() == GameType.COMPUTER_V_PLAYER) {
            launchComputerMove();
        } else if (GameSettings.getInstance().gameType() == GameType.COMPUTER_V_COMPUTER) {
            launchComputerMove();
        }
    }

    public void launchComputerMove() {
        AIDelegate.computerIsThinking(true);
        new Thread(() -> {
            MoveCommand move = AIDelegate.getMove();
            boardUI.makeMoveUI(move.startPosition(), move.endPosition(), true);
            AIDelegate.computerIsThinking(false);
        }).start();
    }

    private void initPlayers() {
        if (NetworkDelegate.getInstance().isModeOnline()) {
            InfoPlayer myInfo = NetworkDelegate.getInstance().infoPlayer();
            InfoPlayer myOpponentInfo = NetworkDelegate.getInstance().infoOpponent();

            Color color = myInfo.color();
            if (color.isWhite()) {
                playerWhite = new InfoPlayer(myInfo.name(), myInfo.color(), false);
                playerBlack = new InfoPlayer(myOpponentInfo.name(), myOpponentInfo.color(), false);
            } else {
                playerWhite = new InfoPlayer(myOpponentInfo.name(), myOpponentInfo.color(), false);
                playerBlack = new InfoPlayer(myInfo.name(), myInfo.color(), false);
            }
        } else {
            boolean whiteTurn = ChessDelegate.currentBoard().isWhiteTurn();

            if (GameSettings.getInstance().gameType() == GameType.PLAYER_V_PLAYER) {
                playerWhite = new InfoPlayer("Player 1", Color.WHITE, false);
                playerBlack = new InfoPlayer("Player 2", Color.BLACK, false);
            } else if (GameSettings.getInstance().gameType() == GameType.PLAYER_V_COMPUTER) {
                if (whiteTurn) {
                    playerWhite = new InfoPlayer("Player", Color.WHITE, false);
                    playerBlack = new InfoPlayer("Computer", Color.BLACK, true);
                } else {
                    playerWhite = new InfoPlayer("Computer", Color.WHITE, true);
                    playerBlack = new InfoPlayer("Player", Color.BLACK, false);
                }
            } else if (GameSettings.getInstance().gameType() == GameType.COMPUTER_V_PLAYER) {
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
        if ((!NetworkDelegate.getInstance().isModeOnline() && !playerWhite.isComputer()) || (playerWhite.isComputer() && playerBlack.isComputer()) || (NetworkDelegate.getInstance().isModeOnline() && NetworkDelegate.getInstance().infoPlayer().color().isWhite())) {
            informationBlackArea = UIFactory.createPanel("INFORMATION_BLACK", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 0, 600, 50));
            add(informationBlackArea);

            boardUI = new BoardUI(true);
            boardUI.addMoveDoneListener(new MoveDoneListener(this));
            boardUI.addMoveFailedListener(new MoveFailedListener(this));
            add(boardUI);

            informationWhiteArea = UIFactory.createPanel("INFORMATION_WHITE", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 650, 600, 50));
            add(informationWhiteArea);
        } else {
            // View from black SIDE
            informationWhiteArea = UIFactory.createPanel("INFORMATION_WHITE", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 0, 600, 50));
            add(informationWhiteArea);

            boardUI = new BoardUI(false);
            boardUI.addMoveDoneListener(new MoveDoneListener(this));
            boardUI.addMoveFailedListener(new MoveFailedListener(this));
            add(boardUI);

            informationBlackArea = UIFactory.createPanel("INFORMATION_BLACK", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 650, 600, 50));
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

        Board currentBoard = ChessDelegate.currentBoard();

        UIFactory.createTextField(informationWhiteArea, "WHITE_PLAYER", playerWhite.name() + (currentBoard.isWhiteTurn() ? " - your turn" : ""), java.awt.Color.WHITE, java.awt.Color.BLACK);
        UIFactory.createTextField(informationWhiteArea, "ENNEMY_BLACK_PIECES_REMOVED", Calculator.giveRemovedPieces(currentBoard, Color.BLACK), java.awt.Color.WHITE, java.awt.Color.BLACK);
        informationWhiteArea.doLayout();

        UIFactory.createTextField(informationBlackArea, "BLACK_PLAYER", playerBlack.name() + (!currentBoard.isWhiteTurn() ? " - your turn" : ""), java.awt.Color.DARK_GRAY, java.awt.Color.WHITE);
        UIFactory.createTextField(informationBlackArea, "ENNEMY_WHITE_PIECES_REMOVED", Calculator.giveRemovedPieces(currentBoard, Color.WHITE), java.awt.Color.DARK_GRAY, java.awt.Color.WHITE);
        informationBlackArea.doLayout();
    }

    public void reset() {
        this.updateInformationArea();
        boardUI.resetBoard();
    }
}
