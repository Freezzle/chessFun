package ch.claudedy.chess;

import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.systems.GameType;
import ch.claudedy.chess.systems.LoaderFromFile;
import ch.claudedy.chess.systems.StockFish;
import ch.claudedy.chess.systems.SystemConfig;
import ch.claudedy.chess.ui.ChessBoard;
import ch.claudedy.chess.ui.InfoPlayer;
import ch.claudedy.chess.ui.UIFactory;
import ch.claudedy.chess.utils.Calculator;
import ch.claudedy.chess.utils.FenUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ApplicationSwing extends JFrame {

    private StockFish stockFish;

    // CHESS (TRUTH)
    @Getter
    private Chess chess;

    private InfoPlayer playerWhite;
    private InfoPlayer playerBlack;

    @Getter
    private boolean isComputerThinking = false;

    // VIEWS (ONLY VIEW PURPOSE)
    private final JLayeredPane layeredPane;
    private ChessBoard chessBoard;
    private JPanel informationWhiteArea;
    private JPanel informationBlackArea;

    public static void main(String[] args) {
        // Configuration of the Swing Application
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

    public ApplicationSwing() {
        //  Create a root layer
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(600, 700));
        layeredPane.setBounds(new Rectangle(0, 0, 600, 700));
        getContentPane().add(layeredPane);

        // Launch the game (initm, etc...)
        startNewGame();
    }

    private synchronized void startNewGame() {
        // Load the chess board from a file
        chess = LoaderFromFile.readFile(SystemConfig.BOARD);

        if (SystemConfig.GAME_TYPE == GameType.PLAYER_V_PLAYER) {
            playerWhite = new InfoPlayer("Player 1", "1000", Color.WHITE, false);
            playerBlack = new InfoPlayer("Player 2", "1000", Color.BLACK, false);
            this.createSquares();
            this.reset();
            chessBoard.printPreviousMove(chess.actualMove());
        } else if (SystemConfig.GAME_TYPE == GameType.PLAYER_V_COMPUTER) {
            if (chess.currentBoard().isWhiteCurrentPlayer()) {
                playerWhite = new InfoPlayer("Player", "1000", Color.WHITE, false);
                playerBlack = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.BLACK, true);
            } else {
                playerWhite = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.WHITE, true);
                playerBlack = new InfoPlayer("Player", "1000", Color.BLACK, false);
            }
            this.createSquares();
            this.reset();
            chessBoard.printPreviousMove(chess.actualMove());
            launchStockFishEngine();
        } else if (SystemConfig.GAME_TYPE == GameType.COMPUTER_V_PLAYER) {
            if (chess.currentBoard().isWhiteCurrentPlayer()) {
                playerWhite = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.WHITE, true);
                playerBlack = new InfoPlayer("Player", "1000", Color.BLACK, false);
            } else {
                playerWhite = new InfoPlayer("Player", "1000", Color.WHITE, false);
                playerBlack = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.BLACK, true);
            }
            this.createSquares();
            this.reset();
            chessBoard.printPreviousMove(chess.actualMove());
            launchStockFishEngine();
            launchComputerMove();
        } else {
            playerWhite = new InfoPlayer("Computer 1", SystemConfig.ELO_COMPUTER, Color.WHITE, true);
            playerBlack = new InfoPlayer("Computer 2", SystemConfig.ELO_COMPUTER, Color.BLACK, true);
            this.createSquares();
            this.reset();
            chessBoard.printPreviousMove(chess.actualMove());

            launchStockFishEngine();
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

    private void launchStockFishEngine() {
        // If there is an computer, launch the stockfish engine
        if (playerBlack.isComputer() || playerWhite.isComputer()) {
            stockFish = new StockFish();
            stockFish.startEngine();
        }
    }

    public void launchComputerMove() {
        this.isComputerThinking = true;

        new Thread(() -> {
            String bestMove = stockFish.getBestMove(FenUtils.boardToFen(chess.currentBoard()), SystemConfig.MOVETIME_STOCKFISH);
            this.manageAfterMove(chess.makeMove(MoveCommand.convert(bestMove)));
            this.isComputerThinking = false;
        }).start();
    }

    private synchronized void createSquares() {
        if (this.layeredPane.getComponentCount() != 0) {
            this.layeredPane.removeAll();
        }

        Square[][] squares = chess.currentBoard().squares();

        // PANEL INFO TOP
        if (!playerWhite.isComputer() || (playerWhite.isComputer() && playerBlack.isComputer())) {
            informationBlackArea = UIFactory.createPanel("INFORMATION_BLACK", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 0, 600, 50));
            layeredPane.add(informationBlackArea);
        } else {
            informationWhiteArea = UIFactory.createPanel("INFORMATION_WHITE", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 0, 600, 50));
            layeredPane.add(informationWhiteArea);
        }

        // PANEL BOARD
        chessBoard = new ChessBoard(this);
        layeredPane.add(chessBoard);

        int counter = 0;
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                JPanel squarePanel = new JPanel(new BorderLayout());
                Square currentSquare = squares[x][y];
                squarePanel.setName(currentSquare.tile().name());
                chessBoard.addSquare(squarePanel, counter);
                counter++;
            }
        }

        // PANEL INFO BOTTOM
        if (!playerWhite.isComputer() || (playerWhite.isComputer() && playerBlack.isComputer())) {
            informationWhiteArea = UIFactory.createPanel("INFORMATION_WHITE", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 650, 600, 50));
            layeredPane.add(informationWhiteArea);
        } else {
            informationBlackArea = UIFactory.createPanel("INFORMATION_BLACK", new GridLayout(2, 1), new Dimension(600, 50), new Rectangle(0, 650, 600, 50));
            layeredPane.add(informationBlackArea);
        }
    }

    private synchronized void printInformationArea() {
        if (informationWhiteArea.getComponentCount() != 0) {
            informationWhiteArea.removeAll();
        }
        if (informationBlackArea.getComponentCount() != 0) {
            informationBlackArea.removeAll();
        }

        Board currentBoard = chess.currentBoard();

        UIFactory.createTextField(informationWhiteArea, "WHITE_PLAYER", playerWhite.getName() + " (" + playerWhite.getElo() + ")" + (currentBoard.isWhiteCurrentPlayer() ? " - your turn" : ""), java.awt.Color.WHITE, java.awt.Color.BLACK);
        UIFactory.createTextField(informationWhiteArea, "ENNEMY_BLACK_PIECES_REMOVED", Calculator.giveRemovedPieces(currentBoard, Color.BLACK), java.awt.Color.WHITE, java.awt.Color.BLACK);
        informationWhiteArea.doLayout();

        UIFactory.createTextField(informationBlackArea, "BLACK_PLAYER", playerBlack.getName() + " (" + playerBlack.getElo() + ")" + (!currentBoard.isWhiteCurrentPlayer() ? " - your turn" : ""), java.awt.Color.DARK_GRAY, java.awt.Color.WHITE);
        UIFactory.createTextField(informationBlackArea, "ENNEMY_WHITE_PIECES_REMOVED", Calculator.giveRemovedPieces(currentBoard, Color.WHITE), java.awt.Color.DARK_GRAY, java.awt.Color.WHITE);
        informationBlackArea.doLayout();
    }

    private synchronized void reset() {
        chessBoard.initSelectedPieceTile();
        this.printInformationArea();
        chessBoard.resetBackgroundTiles();
        chessBoard.printPieces();

        chessBoard.doLayout();
    }

    public synchronized void manageAfterMove(MoveStatus moveDoneStatus) {
        // If the move was authorized
        if (moveDoneStatus.isOk()) { // MOVE OK
            if (chess.gameStatus().isGameOver()) { // GAME OVER
                if (SystemConfig.GAME_TYPE.containsAComputer()) {
                    stockFish.stopEngine();
                }
            }
        }

        this.reset();
    }
}
