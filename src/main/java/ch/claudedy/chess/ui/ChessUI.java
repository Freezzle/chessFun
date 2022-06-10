package ch.claudedy.chess.ui;

import ch.claudedy.chess.basis.Board;
import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.MoveCommand;
import ch.claudedy.chess.systems.GameType;
import ch.claudedy.chess.systems.SystemConfig;
import ch.claudedy.chess.ui.event.MoveDoneListener;
import ch.claudedy.chess.ui.event.MoveFailedListener;
import ch.claudedy.chess.utils.Calculator;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;

@Accessors(fluent = true)
public class ChessUI extends JFrame {

    // Main layer of the application
    private final JLayeredPane mainLayer;

    // UI Child
    private BoardUI boardUI;
    private JPanel informationWhiteArea;
    private JPanel informationBlackArea;
    @Getter
    private InfoPlayer playerWhite;
    @Getter
    private InfoPlayer playerBlack;

    public ChessUI() {
        mainLayer = new JLayeredPane();
        mainLayer.setPreferredSize(new Dimension(600, 700));
        mainLayer.setBounds(new Rectangle(0, 0, 600, 700));
        getContentPane().add(mainLayer);

        // Launch the game (init, etc...)
        startNewGame();
    }

    private synchronized void startNewGame() {
        ChessDelegate.startNewGame();

        this.initPlayers();
        this.initLayers();
        this.reset();

        this.manageComputerGame();
    }

    private void manageComputerGame() {
        if (SystemConfig.GAME_TYPE == GameType.COMPUTER_V_PLAYER) {
            launchComputerMove();
        } else if (SystemConfig.GAME_TYPE == GameType.COMPUTER_V_COMPUTER) {
            launchComputerMove();
        }
    }

    public void launchComputerMove() {
        AIDelegate.computerIsThinking(true);
        new Thread(() -> {
            MoveCommand move = AIDelegate.getMove();
            boardUI.makeMoveUI(move.startPosition(), move.endPosition());
            AIDelegate.computerIsThinking(false);
        }).start();
    }

    private void initPlayers() {
        boolean whiteTurn = ChessDelegate.currentBoard().isWhiteTurn();

        if (SystemConfig.GAME_TYPE == GameType.PLAYER_V_PLAYER) {
            playerWhite = new InfoPlayer("Player 1", "1000", Color.WHITE, false);
            playerBlack = new InfoPlayer("Player 2", "1000", Color.BLACK, false);
        } else if (SystemConfig.GAME_TYPE == GameType.PLAYER_V_COMPUTER) {
            if (whiteTurn) {
                playerWhite = new InfoPlayer("Player", "1000", Color.WHITE, false);
                playerBlack = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.BLACK, true);
            } else {
                playerWhite = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.WHITE, true);
                playerBlack = new InfoPlayer("Player", "1000", Color.BLACK, false);
            }
        } else if (SystemConfig.GAME_TYPE == GameType.COMPUTER_V_PLAYER) {
            if (whiteTurn) {
                playerWhite = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.WHITE, true);
                playerBlack = new InfoPlayer("Player", "1000", Color.BLACK, false);
            } else {
                playerWhite = new InfoPlayer("Player", "1000", Color.WHITE, false);
                playerBlack = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.BLACK, true);
            }
        } else {
            playerWhite = new InfoPlayer("Computer 1", SystemConfig.ELO_COMPUTER, Color.WHITE, true);
            playerBlack = new InfoPlayer("Computer 2", SystemConfig.ELO_COMPUTER, Color.BLACK, true);
        }
    }

    private void initLayers() {
        if (this.mainLayer.getComponentCount() != 0) {
            this.mainLayer.removeAll();
        }

        // View from white SIDE
        if (!playerWhite.isComputer() || (playerWhite.isComputer() && playerBlack.isComputer())) {
            informationBlackArea = UIFactory.createPanel("INFORMATION_BLACK", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 0, 600, 50));
            mainLayer.add(informationBlackArea);

            boardUI = new BoardUI(true);
            boardUI.addMoveDoneListener(new MoveDoneListener(this));
            boardUI.addMoveFailedListener(new MoveFailedListener(this));
            mainLayer.add(boardUI);

            informationWhiteArea = UIFactory.createPanel("INFORMATION_WHITE", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 650, 600, 50));
            mainLayer.add(informationWhiteArea);
        } else {
            // View from black SIDE
            informationWhiteArea = UIFactory.createPanel("INFORMATION_WHITE", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 0, 600, 50));
            mainLayer.add(informationWhiteArea);

            boardUI = new BoardUI(false);
            boardUI.addMoveDoneListener(new MoveDoneListener(this));
            boardUI.addMoveFailedListener(new MoveFailedListener(this));
            mainLayer.add(boardUI);

            informationBlackArea = UIFactory.createPanel("INFORMATION_BLACK", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 650, 600, 50));
            mainLayer.add(informationBlackArea);
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

        UIFactory.createTextField(informationWhiteArea, "WHITE_PLAYER", playerWhite.name() + " (" + playerWhite.elo() + ")" + (currentBoard.isWhiteTurn() ? " - your turn" : ""), java.awt.Color.WHITE, java.awt.Color.BLACK);
        UIFactory.createTextField(informationWhiteArea, "ENNEMY_BLACK_PIECES_REMOVED", Calculator.giveRemovedPieces(currentBoard, Color.BLACK), java.awt.Color.WHITE, java.awt.Color.BLACK);
        informationWhiteArea.doLayout();

        UIFactory.createTextField(informationBlackArea, "BLACK_PLAYER", playerBlack.name() + " (" + playerBlack.elo() + ")" + (!currentBoard.isWhiteTurn() ? " - your turn" : ""), java.awt.Color.DARK_GRAY, java.awt.Color.WHITE);
        UIFactory.createTextField(informationBlackArea, "ENNEMY_WHITE_PIECES_REMOVED", Calculator.giveRemovedPieces(currentBoard, Color.WHITE), java.awt.Color.DARK_GRAY, java.awt.Color.WHITE);
        informationBlackArea.doLayout();
    }

    public void reset() {
        this.updateInformationArea();
        boardUI.resetBoard();
    }
}
