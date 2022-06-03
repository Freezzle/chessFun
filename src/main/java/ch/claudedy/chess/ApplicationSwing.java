package ch.claudedy.chess;

import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.systems.GameType;
import ch.claudedy.chess.systems.LoaderFromFile;
import ch.claudedy.chess.systems.StockFish;
import ch.claudedy.chess.systems.SystemConfig;
import ch.claudedy.chess.ui.InfoPlayer;
import ch.claudedy.chess.utils.Calculator;
import ch.claudedy.chess.utils.FenUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationSwing extends JFrame implements MouseListener, MouseMotionListener {

    private static final java.awt.Color BLACK_SQUARE = new java.awt.Color(75, 115, 145);
    private static final java.awt.Color WHITE_SQUARE = new java.awt.Color(230, 230, 210);

    private static final java.awt.Color PREVIOUS_MOVE_BLACK_SQUARE = new java.awt.Color(100, 180, 100);
    private static final java.awt.Color PREVIOUS_MOVE_WHITE_SQUARE = new java.awt.Color(130, 200, 130);

    private static final java.awt.Color SELECTED_PIECE_BLACK_SQUARE = new java.awt.Color(100, 155, 180);
    private static final java.awt.Color SELECTED_PIECE_WHITE_SQUARE = new java.awt.Color(130, 170, 200);

    private static final java.awt.Color ANALYSE_CLICKED_BLACK_SQUARE = new java.awt.Color(200, 100, 90);
    private static final java.awt.Color ANALYSE_CLICKED_WHITE_SQUARE = new java.awt.Color(235, 125, 100);
    private static final java.awt.Color LEGAL_MOVE_BLACK_SQUARE = new java.awt.Color(180, 123, 100);
    private static final java.awt.Color LEGAL_MOVE_WHITE_SQUARE = new java.awt.Color(240, 192, 180);

    private static final int LEFT_CLICK = 1;
    private static final int RIGHT_CLICK = 3;

    private static final Map<String, ImageIcon> piecesImages = new HashMap<>();

    private StockFish stockFish;

    // CHESS (TRUTH)
    private Chess chess;

    private InfoPlayer playerWhite;
    private InfoPlayer playerBlack;

    private boolean isComputerThinking = false;

    // VIEWS (ONLY VIEW PURPOSE)
    private final JLayeredPane layeredPane;
    private JPanel chessBoard;
    private JPanel informationWhiteArea;
    private JPanel informationBlackArea;

    private final Map<String, Integer> squaresBoardUI = new HashMap<>();

    // CHOICE ABOUT PLAYER
    private Tile selectedPieceTile;

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

        if(SystemConfig.GAME_TYPE == GameType.PLAYER_V_PLAYER) {
            playerWhite = new InfoPlayer("Player 1", "1000", Color.WHITE, false);
            playerBlack = new InfoPlayer("Player 2", "1000", Color.BLACK, false);
            this.createSquares();
            this.reset();
            this.printPreviousMove(chess.actualMove());
        } else if(SystemConfig.GAME_TYPE == GameType.PLAYER_V_COMPUTER) {
            playerWhite = new InfoPlayer("Player", "1000", Color.WHITE, false);
            playerBlack = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.BLACK, true);
            this.createSquares();
            this.reset();
            this.printPreviousMove(chess.actualMove());
            launchStockFishEngine();
        } else if(SystemConfig.GAME_TYPE == GameType.COMPUTER_V_PLAYER) {
            playerWhite = new InfoPlayer("Computer", SystemConfig.ELO_COMPUTER, Color.WHITE, true);
            playerBlack = new InfoPlayer("Player", "1000", Color.BLACK, false);
            this.createSquares();
            this.reset();
            this.printPreviousMove(chess.actualMove());
            launchStockFishEngine();
            launchComputerMove();
        } else {
            playerWhite = new InfoPlayer("Computer 1", SystemConfig.ELO_COMPUTER, Color.WHITE, true);
            playerBlack = new InfoPlayer("Computer 2", SystemConfig.ELO_COMPUTER, Color.BLACK, true);
            this.createSquares();
            this.reset();
            this.printPreviousMove(chess.actualMove());

            launchStockFishEngine();
            this.isComputerThinking = true;
            new Thread(() -> {
                while (!chess.gameStatus().isGameOver()) {
                    if (chess.gameStatus().isGameWaitingMove()) {
                        String bestMove = stockFish.getBestMove(FenUtils.boardToFen(chess.currentBoard()), SystemConfig.MOVETIME_STOCKFISH);
                        this.manageAfterMove(chess.makeMove(MoveCommand.convert(bestMove)));
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

    private void launchComputerMove() {
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

        int counter = 0;
        if (!playerWhite.isComputer() || (playerWhite.isComputer() && playerBlack.isComputer())) {
            informationBlackArea = new JPanel();
            informationBlackArea.setName("INFORMATION_BLACK");
            informationBlackArea.setLayout(new GridLayout(2, 1));
            informationBlackArea.setPreferredSize(new Dimension(600, 50));
            informationBlackArea.setBounds(new Rectangle(0, 0, 600, 50));
            layeredPane.add(informationBlackArea);

            // Add a chess board layer to the root Layer
            chessBoard = new JPanel();
            chessBoard.setName("BOARD");
            chessBoard.setLayout(new GridLayout(8, 8));
            chessBoard.setPreferredSize(new Dimension(600, 600));
            chessBoard.setBounds(new Rectangle(0, 50, 600, 600));
            chessBoard.setBackground(java.awt.Color.blue);
            chessBoard.addMouseListener(this);
            chessBoard.addMouseMotionListener(this);
            layeredPane.add(chessBoard);

            for (int y = 7; y >= 0; y--) {
                for (int x = 0; x <= 7; x++) {
                    JPanel squarePanel = new JPanel(new BorderLayout());
                    Square currentSquare = squares[x][y];
                    squarePanel.setName(currentSquare.tile().name());
                    chessBoard.add(squarePanel, counter);
                    squaresBoardUI.put(currentSquare.tile().name(), counter);
                    counter++;
                }
            }

            informationWhiteArea = new JPanel();
            informationWhiteArea.setName("INFORMATION_WHITE");
            informationWhiteArea.setLayout(new GridLayout(2, 1));
            informationWhiteArea.setPreferredSize(new Dimension(600, 50));
            informationWhiteArea.setBounds(new Rectangle(0, 650, 600, 50));
            layeredPane.add(informationWhiteArea);
        } else {
            informationWhiteArea = new JPanel();
            informationWhiteArea.setName("INFORMATION_WHITE");
            informationWhiteArea.setLayout(new GridLayout(2, 1));
            informationWhiteArea.setPreferredSize(new Dimension(600, 50));
            informationWhiteArea.setBounds(new Rectangle(0, 0, 600, 50));
            layeredPane.add(informationWhiteArea);

            // Add a chess board layer to the root Layer
            chessBoard = new JPanel();
            chessBoard.setName("BOARD");
            chessBoard.setLayout(new GridLayout(8, 8));
            chessBoard.setPreferredSize(new Dimension(600, 600));
            chessBoard.setBounds(new Rectangle(0, 50, 600, 600));
            chessBoard.setBackground(java.awt.Color.blue);
            chessBoard.addMouseListener(this);
            chessBoard.addMouseMotionListener(this);
            layeredPane.add(chessBoard);

            for (int y = 0; y <= 7; y++) {
                for (int x = 7; x >= 0; x--) {
                    JPanel squarePanel = new JPanel(new BorderLayout());
                    Square currentSquare = squares[x][y];
                    squarePanel.setName(currentSquare.tile().name());
                    chessBoard.add(squarePanel, counter);
                    squaresBoardUI.put(currentSquare.tile().name(), counter);
                    counter++;
                }
            }

            informationBlackArea = new JPanel();
            informationBlackArea.setName("INFORMATION_BLACK");
            informationBlackArea.setLayout(new GridLayout(2, 1));
            informationBlackArea.setPreferredSize(new Dimension(600, 50));
            informationBlackArea.setBounds(new Rectangle(0, 650, 600, 50));
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

        JTextField panelWhitePlayer = new JTextField(playerWhite.getName() + " (" + playerWhite.getElo() + ")" + (currentBoard.currentPlayer().isWhite() ? " - your turn" : ""));
        panelWhitePlayer.setName("WHITE_PLAYER");
        panelWhitePlayer.setBounds(new Rectangle(600, 50));
        informationWhiteArea.add(panelWhitePlayer);

        JTextField panelBlackPieces = new JTextField(Calculator.giveRemovedPieces(currentBoard, Color.BLACK));
        panelBlackPieces.setName("ENNEMY_BLACK_PIECES_REMOVED");
        panelBlackPieces.setBounds(new Rectangle(600, 50));
        informationWhiteArea.add(panelBlackPieces);

        informationWhiteArea.doLayout();

        JTextField panelBlackPlayer = new JTextField(playerBlack.getName() + " (" + playerBlack.getElo() + ")" + (!currentBoard.currentPlayer().isWhite() ? " - your turn" : ""));
        panelBlackPlayer.setName("BLACK_PLAYER");
        panelBlackPlayer.setBounds(new Rectangle(600, 50));
        informationBlackArea.add(panelBlackPlayer);

        JTextField panelWhitePieces = new JTextField(Calculator.giveRemovedPieces(currentBoard, Color.WHITE));
        panelWhitePieces.setName("ENNEMY_WHITE_PIECES_REMOVED");
        panelWhitePieces.setBounds(new Rectangle(600, 50));
        informationBlackArea.add(panelWhitePieces);

        informationBlackArea.doLayout();
    }

    private synchronized void printPieces() {
        // TODO: 02.06.2022 PERFORMANCE TO IMPROVE HERE
        Square[][] squares = chess.currentBoard().squares();

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {

                Square currentSquare = squares[x][y];
                Component component = getComponentUI(currentSquare.tile());
                if (component instanceof JPanel) {
                    JPanel panel = (JPanel) component;

                    if (currentSquare.piece() != null) {
                        String namePiece = currentSquare.piece().type().getAbrevTechnicalBlack() + "_" + currentSquare.piece().color();

                        if (panel.getComponentCount() != 0 && namePiece.equals(panel.getComponent(0).getName()) && panel.getComponent(0).isVisible()) {
                            continue;
                        } else {
                            if (panel.getComponentCount() != 0) {
                                panel.getComponent(0).setVisible(false);
                            }
                        }

                        ImageIcon imagePiece = piecesImages.get(namePiece);
                        if (imagePiece == null) {
                            imagePiece = new ImageIcon(ClassLoader.getSystemResource("images/" + namePiece + ".png"));
                            piecesImages.put(namePiece, imagePiece);
                        }

                        JLabel piece = new JLabel(imagePiece);
                        piece.setName(namePiece);
                        panel.setFocusable(false);
                        panel.add(piece, 0);
                    } else {
                        if (panel.getComponentCount() != 0) {
                            panel.getComponent(0).setVisible(false);
                        }
                        panel.setFocusable(true);
                    }
                }
            }
        }
    }

    private synchronized void initSelectedPieceTile() {
        if (selectedPieceTile != null) {
            // Reset color for the selected piece tile
            getComponentUI(selectedPieceTile).setBackground(getColorTile(selectedPieceTile.color()));
        }

        selectedPieceTile = null;
    }

    private synchronized void resetBackgroundTiles() {
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                Tile currentTile = chess.currentBoard().squares()[x][y].tile();
                getComponentUI(currentTile).setBackground(getColorTile(currentTile.color()));
            }
        }
        this.printPreviousMove(chess.actualMove());
    }

    private synchronized void reset() {
        this.initSelectedPieceTile();
        this.printInformationArea();
        this.resetBackgroundTiles();
        this.printPieces();

        chessBoard.doLayout();
    }

    private synchronized void printPreviousMove(MoveCommand move) {

        // Reset background previous move
        if (chess.actualMove() != null) {
            getComponentUI(chess.actualMove().startPosition()).setBackground(getColorTile(chess.actualMove().startPosition().color()));
            getComponentUI(chess.actualMove().endPosition()).setBackground(getColorTile(chess.actualMove().endPosition().color()));
        }

        // Colorize the new move backgrounds
        if (move != null) {
            getComponentUI(move.startPosition()).setBackground(getColorTileForPreviousMove(move.startPosition().color()));
            getComponentUI(move.endPosition()).setBackground(getColorTileForPreviousMove(move.endPosition().color()));
        }
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

    public void mouseClicked(MouseEvent e) {
        if (e == null || chess.gameStatus().isGameOver()) {
            return;
        }

        Component tileClicked = getTileUI(e.getX(), e.getY());
        int buttonClicked = e.getButton();

        if (tileClicked != null) {
            if (selectedPieceTile == null && buttonClicked == LEFT_CLICK && !this.isComputerThinking) { // No piece selected and we click left on board (select a piece)
                this.resetBackgroundTiles();

                if (tileClicked instanceof JPanel) {
                    return;
                }

                selectedPieceTile = Tile.getEnum(tileClicked.getParent().getName());

                if (selectedPieceTile == null) {
                    selectedPieceTile = Tile.getEnum(tileClicked.getName());
                }

                getComponentUI(selectedPieceTile).setBackground(getColorTileForSelectedPiece(selectedPieceTile.color()));

                this.colorizeLegalMoves(chess.getLegalMoves(selectedPieceTile));
            } else if (selectedPieceTile != null && buttonClicked == LEFT_CLICK && !this.isComputerThinking) { // Piece already selected and we click left on board again (make move)
                Tile destination = Tile.getEnum(tileClicked.getName());

                if (destination == null) {
                    destination = Tile.getEnum(tileClicked.getParent().getName());
                }

                MoveStatus status = chess.makeMove(new MoveCommand(selectedPieceTile, destination, null));

                // If the move was authorized
                this.manageAfterMove(status);

                if (status.isOk() && SystemConfig.GAME_TYPE.containsAComputer()) {
                    launchComputerMove();
                }
            } else if (e.getButton() == RIGHT_CLICK) { // No piece selected and we click right (print square in red)
                // color background red
                Tile tileSelected = Tile.getEnum(tileClicked.getName());
                if (tileSelected == null) {
                    tileSelected = Tile.getEnum(tileClicked.getParent().getName());
                }

                if (tileSelected.color() == Color.BLACK) {
                    getComponentUI(tileSelected).setBackground(ANALYSE_CLICKED_BLACK_SQUARE);
                } else {
                    getComponentUI(tileSelected).setBackground(ANALYSE_CLICKED_WHITE_SQUARE);
                }
            } else { // Reset the board
                this.selectedPieceTile = null;
                this.resetBackgroundTiles();
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        if (e == null || chess.gameStatus().isGameOver()) {
            return;
        }

        Component tileClicked = getTileUI(e.getX(), e.getY());
        int buttonClicked = e.getButton();

        if (tileClicked != null) {
            if (selectedPieceTile == null && buttonClicked == LEFT_CLICK && !this.isComputerThinking) { // No piece selected and we click left on board (select a piece)
                this.resetBackgroundTiles();

                if (tileClicked instanceof JPanel) {
                    return;
                }

                selectedPieceTile = Tile.getEnum(tileClicked.getParent().getName());

                if (selectedPieceTile == null) {
                    selectedPieceTile = Tile.getEnum(tileClicked.getName());
                }

                getComponentUI(selectedPieceTile).setBackground(getColorTileForSelectedPiece(selectedPieceTile.color()));

                this.colorizeLegalMoves(chess.getLegalMoves(selectedPieceTile));
            }
        }
    }


    public void mouseReleased(MouseEvent e) {
        if (e == null || chess.gameStatus().isGameOver()) {
            return;
        }

        Component tileClicked = getTileUI(e.getX(), e.getY());
        int buttonClicked = e.getButton();

        if (tileClicked != null) {
            if (selectedPieceTile != null && buttonClicked == LEFT_CLICK && !this.isComputerThinking) { // Piece already selected and we click left on board again (make move)
                Tile destination = Tile.getEnum(tileClicked.getName());

                if (destination == null) {
                    destination = Tile.getEnum(tileClicked.getParent().getName());
                }

                MoveStatus status = chess.makeMove(new MoveCommand(selectedPieceTile, destination, null));

                // If the move was authorized
                this.manageAfterMove(status);

                if (status.isOk() && SystemConfig.GAME_TYPE.containsAComputer()) {
                    launchComputerMove();
                }
            }
        }
    }


    public void mouseMoved(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    private Component getTileUI(int x, int y) {
        return chessBoard.findComponentAt(x, y);
    }

    private Component getComponentUI(Tile tile) {
        return chessBoard.getComponent(this.squaresBoardUI.get(tile.name()));
    }

    private void colorizeLegalMoves(List<Tile> tiles) {
        tiles.forEach(tile -> {
            if (tile.color() == Color.BLACK) {
                getComponentUI(tile).setBackground(LEGAL_MOVE_BLACK_SQUARE);
            } else {
                getComponentUI(tile).setBackground(LEGAL_MOVE_WHITE_SQUARE);
            }
        });
    }

    private java.awt.Color getColorTile(Color colorTile) {
        return colorTile.isSameColor(Color.BLACK) ? BLACK_SQUARE : WHITE_SQUARE;
    }

    private java.awt.Color getColorTileForPreviousMove(Color colorTile) {
        return !colorTile.isWhite() ? PREVIOUS_MOVE_BLACK_SQUARE : PREVIOUS_MOVE_WHITE_SQUARE;
    }

    private java.awt.Color getColorTileForSelectedPiece(Color colorTile) {
        return !colorTile.isWhite() ? SELECTED_PIECE_BLACK_SQUARE : SELECTED_PIECE_WHITE_SQUARE;
    }
}
