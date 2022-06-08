package ch.claudedy.chess;

import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.systems.GameType;
import ch.claudedy.chess.systems.LoaderFromFile;
import ch.claudedy.chess.systems.StockFish;
import ch.claudedy.chess.systems.SystemConfig;
import ch.claudedy.chess.ui.BoardUI;
import ch.claudedy.chess.ui.InfoPlayer;
import ch.claudedy.chess.ui.UIFactory;
import ch.claudedy.chess.utils.Calculator;
import ch.claudedy.chess.utils.FenUtils;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

@Accessors(fluent = true)
public class ApplicationSwing extends JFrame {

    // Main layer of the application
    private final JLayeredPane mainLayer;

    // Main logic of the game
    @Getter
    private Chess chess;
    
    // Computer
    @Getter
    private boolean isComputerThinking = false;
    private StockFish stockFish;

    // UI
    private BoardUI boardUI;
    private JPanel informationWhiteArea;
    private JPanel informationBlackArea;
    private InfoPlayer playerWhite;
    private InfoPlayer playerBlack;

    public ApplicationSwing() {
        mainLayer = new JLayeredPane();
        mainLayer.setPreferredSize(new Dimension(600, 700));
        mainLayer.setBounds(new Rectangle(0, 0, 600, 700));
        getContentPane().add(mainLayer);

        // Launch the game (init, etc...)
        startNewGame();
    }

    public static void main(String[] args) {
        ApplicationSwing frame = new ApplicationSwing();
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    private synchronized void startNewGame() {
        // Load the chess game from a file
        chess = LoaderFromFile.readFile(SystemConfig.BOARD);

        this.initPlayers();
        this.initLayers();
        this.reset();

        if (SystemConfig.GAME_TYPE.containsInLessAComputer()) {
            launchStockFishEngine();
        }

        if (SystemConfig.GAME_TYPE == GameType.COMPUTER_V_PLAYER) {
            launchComputerMove();
        } else if (SystemConfig.GAME_TYPE == GameType.COMPUTER_V_COMPUTER) {
            this.isComputerThinking = true;
            new Thread(() -> {
                while (!chess.gameStatus().isGameOver()) {
                    if (chess.gameStatus().isGameWaitingMove()) {
                        this.isComputerThinking = true;
                        String bestMove = stockFish.getBestMove(FenUtils.boardToFen(chess.currentBoard()), SystemConfig.MOVETIME_STOCKFISH);
                        this.manageAfterMove(chess.makeMove(MoveCommand.convert(bestMove)));
                        this.isComputerThinking = false;
                    }
                }
                Thread.currentThread().interrupt();
                System.exit(0);
            }).start();
        }
    }

    private void initPlayers() {
        if (SystemConfig.GAME_TYPE == GameType.PLAYER_V_PLAYER) {
            playerWhite = new InfoPlayer("Player 1", "1000", Color.WHITE, false);
            playerBlack = new InfoPlayer("Player 2", "1000", Color.BLACK, false);
        } else if (SystemConfig.GAME_TYPE == GameType.PLAYER_V_COMPUTER) {
            if (chess.currentBoard().isWhiteTurn()) {
                playerWhite = new InfoPlayer("Player", "1000", Color.WHITE, false);
                playerBlack = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.BLACK, true);
            } else {
                playerWhite = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.WHITE, true);
                playerBlack = new InfoPlayer("Player", "1000", Color.BLACK, false);
            }
        } else if (SystemConfig.GAME_TYPE == GameType.COMPUTER_V_PLAYER) {
            if (chess.currentBoard().isWhiteTurn()) {
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

    private void launchStockFishEngine() {
        stockFish = new StockFish();
        stockFish.startEngine();
    }

    public void launchComputerMove() {
        this.isComputerThinking = true;

        new Thread(() -> {
            String bestMove = stockFish.getBestMove(FenUtils.boardToFen(chess.currentBoard()), SystemConfig.MOVETIME_STOCKFISH);
            this.manageAfterMove(chess.makeMove(MoveCommand.convert(bestMove)));
            this.isComputerThinking = false;
        }).start();
    }

    private synchronized void initLayers() {
        if (this.mainLayer.getComponentCount() != 0) {
            this.mainLayer.removeAll();
        }

        Square[][] squares = chess.currentBoard().squares();

        // View from white SIDE
        if (!playerWhite.isComputer() || (playerWhite.isComputer() && playerBlack.isComputer())) {
            informationBlackArea = UIFactory.createPanel("INFORMATION_BLACK", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 0, 600, 50));
            mainLayer.add(informationBlackArea);

            boardUI = new BoardUI(this);
            mainLayer.add(boardUI);

            int counter = 0;
            for (int y = 7; y >= 0; y--) {
                for (int x = 0; x <= 7; x++) {
                    boardUI.createSquare(squares[x][y], counter);
                    counter++;
                }
            }

            informationWhiteArea = UIFactory.createPanel("INFORMATION_WHITE", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 650, 600, 50));
            mainLayer.add(informationWhiteArea);
        } else {
            // View from black SIDE
            informationWhiteArea = UIFactory.createPanel("INFORMATION_WHITE", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 0, 600, 50));
            mainLayer.add(informationWhiteArea);


            boardUI = new BoardUI(this);
            mainLayer.add(boardUI);

            int counter = 0;
            for (int y = 0; y <= 7; y++) {
                for (int x = 7; x >= 0; x--) {
                    boardUI.createSquare(squares[x][y], counter);
                    counter++;
                }
            }

            informationBlackArea = UIFactory.createPanel("INFORMATION_BLACK", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 650, 600, 50));
            mainLayer.add(informationBlackArea);
        }
    }

    private synchronized void updateInformationArea() {
        if (informationWhiteArea.getComponentCount() != 0) {
            informationWhiteArea.removeAll();
        }
        if (informationBlackArea.getComponentCount() != 0) {
            informationBlackArea.removeAll();
        }

        Board currentBoard = chess.currentBoard();

        UIFactory.createTextField(informationWhiteArea, "WHITE_PLAYER", playerWhite.name() + " (" + playerWhite.elo() + ")" + (currentBoard.isWhiteTurn() ? " - your turn" : ""), java.awt.Color.WHITE, java.awt.Color.BLACK);
        UIFactory.createTextField(informationWhiteArea, "ENNEMY_BLACK_PIECES_REMOVED", Calculator.giveRemovedPieces(currentBoard, Color.BLACK), java.awt.Color.WHITE, java.awt.Color.BLACK);
        informationWhiteArea.doLayout();

        UIFactory.createTextField(informationBlackArea, "BLACK_PLAYER", playerBlack.name() + " (" + playerBlack.elo() + ")" + (!currentBoard.isWhiteTurn() ? " - your turn" : ""), java.awt.Color.DARK_GRAY, java.awt.Color.WHITE);
        UIFactory.createTextField(informationBlackArea, "ENNEMY_WHITE_PIECES_REMOVED", Calculator.giveRemovedPieces(currentBoard, Color.WHITE), java.awt.Color.DARK_GRAY, java.awt.Color.WHITE);
        informationBlackArea.doLayout();
    }

    private synchronized void reset() {
        this.updateInformationArea();
        boardUI.resetBoard();
    }

    public synchronized void manageAfterMove(MoveStatus moveDoneStatus) {
        // If the move was authorized
        if (moveDoneStatus.isOk()) { // MOVE OK
            if (chess.gameStatus().isGameOver()) { // GAME OVER
                if (SystemConfig.GAME_TYPE.containsInLessAComputer()) {
                    stockFish.stopEngine();
                }
            }
        }

        this.reset();
    }
}
