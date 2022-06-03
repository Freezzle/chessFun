package ch.claudedy.chess;

import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.systems.GameType;
import ch.claudedy.chess.systems.LoaderFromFile;
import ch.claudedy.chess.systems.StockFish;
import ch.claudedy.chess.systems.SystemConfig;
import ch.claudedy.chess.utils.FenUtils;

import javax.swing.*;
import java.awt.Color;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationSwing extends JFrame implements MouseListener, MouseMotionListener {

    private static final Color BLACK_SQUARE = new Color(75, 115, 145);
    private static final Color WHITE_SQUARE = new Color(230, 230, 210);

    private static final Color PREVIOUS_MOVE_BLACK_SQUARE = new Color(100, 180, 100);
    private static final Color PREVIOUS_MOVE_WHITE_SQUARE = new Color(130, 200, 130);

    private static final Color ANALYSE_CLICKED_BLACK_SQUARE = new Color(200, 100, 90);
    private static final Color ANALYSE_CLICKED_WHITE_SQUARE = new Color(235, 125, 100);
    private static final Color LEGAL_MOVE_BLACK_SQUARE = new Color(180, 123, 100);
    private static final Color LEGAL_MOVE_WHITE_SQUARE = new Color(240, 192, 180);

    private static final int LEFT_CLICK = 1;
    private static final int MIDDLE_CLICK = 2;
    private static final int RIGHT_CLICK = 3;

    private static final Map<String, ImageIcon> piecesImages = new HashMap<>();

    private StockFish stockFish;

    // CHESS (TRUTH)
    private Chess chess;

    private boolean isComputerThinking = false;
    private boolean isWhiteView = SystemConfig.IS_WHITE_VIEW;

    // VIEWS (ONLY VIEW PURPOSE)
    private final JLayeredPane layeredPane;
    private JPanel chessBoard;
    private JPanel informationArea;

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
    }

    public ApplicationSwing() {

        Dimension boardSize = new Dimension(600, 700);

        //  Create a root layer
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(boardSize);
        layeredPane.addMouseListener(this);
        layeredPane.addMouseMotionListener(this);
        getContentPane().add(layeredPane);

        // Launch the game (initm, etc...)
        this.startNewGame();
    }

    private synchronized void startNewGame() {
        Dimension boardSize = new Dimension(600, 700);
        if (this.layeredPane.getComponentCount() != 0) {
            this.layeredPane.removeAll();
        }

        // Add a chess board layer to the root Layer
        chessBoard = new JPanel();
        chessBoard.setName("BOARD");
        chessBoard.setLayout(new GridLayout(8, 8));
        chessBoard.setPreferredSize(boardSize);
        chessBoard.setBounds(0, 0, 600, 600);
        layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);

        // Add an information layer to the root layer
        informationArea = new JPanel();
        informationArea.setName("INFORMATION");
        informationArea.setLayout(new GridLayout(3, 1));
        informationArea.setPreferredSize(boardSize);
        informationArea.setBounds(0, 600, 600, 100);
        layeredPane.add(informationArea, JLayeredPane.DEFAULT_LAYER);

        // Load the chess board from a file
        chess = LoaderFromFile.readFile(SystemConfig.BOARD);

        if (SystemConfig.GAME_TYPE.containsAComputer()) {
            stockFish = new StockFish();
            stockFish.startEngine();
        }

        this.createSquares();

        this.reset();
        this.printPreviousMove(chess.actualMove());

        if (SystemConfig.GAME_TYPE == GameType.COMPUTER_V_PLAYER) {
            String bestMove = stockFish.getBestMove(FenUtils.boardToFen(chess.currentBoard()), SystemConfig.MOVETIME_STOCKFISH);
            MoveFeedBack status = chess.makeMove(MoveCommand.convert(bestMove));
            this.manageAfterMove(status);
        } else if (SystemConfig.GAME_TYPE == GameType.COMPUTER_V_COMPUTER) {

            Thread thread = new Thread(() -> {
                int counter = 0;
                while (counter <= 100 && chess.status() == MoveFeedBack.RUNNING) {
                    launchComputerMove();

                    counter++;
                }

                Thread.currentThread().interrupt();
            });

            thread.start();
        }
    }

    private void launchComputerMove() {
        String bestMove = stockFish.getBestMove(FenUtils.boardToFen(chess.currentBoard()), SystemConfig.MOVETIME_STOCKFISH);
        this.manageAfterMove(chess.makeMove(MoveCommand.convert(bestMove)));
    }

    private synchronized void printInformationArea() {
        if (informationArea.getComponentCount() != 0) {
            informationArea.removeAll();
        }

        Board currentBoard = chess.currentBoard();

        JTextField panelCurrentPlayer = new JTextField(currentBoard.currentPlayer().isWhite() ? "White's turn" : "Black's turn");
        panelCurrentPlayer.setName("CURRENT_PLAYER");
        informationArea.add(panelCurrentPlayer);

        String lastMoveNotation = (chess.actualMove() != null) ? chess.actualMove().convert() : "-";
        JTextField panelFen = new JTextField(FenUtils.boardToFen(currentBoard) + ";" + lastMoveNotation);
        panelFen.setName("FEN");
        informationArea.add(panelFen);

        JTextField panelGameStatus = new JTextField(chess.status().name());
        panelGameStatus.setName("GAME_STATUS");
        informationArea.add(panelGameStatus);

        informationArea.doLayout();
    }

    private synchronized void createSquares() {
        chessBoard.removeAll();

        Square[][] squares = chess.currentBoard().squares();

        int counter = 0;
        if(isWhiteView) {
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
        } else {
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
        }
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

                        if(panel.getComponentCount() != 0 && namePiece.equals(panel.getComponent(0).getName()) && panel.getComponent(0).isVisible()) {
                            continue;
                        } else {
                            if (panel.getComponentCount() != 0) {
                                panel.getComponent(0).setVisible(false);
                            }
                        }

                        ImageIcon imagePiece = piecesImages.get(namePiece);
                        if(imagePiece == null) {
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
            getComponentUI(move.startPosition()).setBackground(getColorTileAboutMove(move.startPosition().color()));
            getComponentUI(move.endPosition()).setBackground(getColorTileAboutMove(move.endPosition().color()));
        }
    }

    public void mouseClicked(MouseEvent e) {
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

                getComponentUI(selectedPieceTile).setBackground(getColorTileAboutMove(selectedPieceTile.color()));

                this.colorizeLegalMoves(chess.getLegalMoves(selectedPieceTile));
            } else if (selectedPieceTile != null && buttonClicked == LEFT_CLICK && !this.isComputerThinking) { // Piece already selected and we click left on board again (make move)
                Tile destination = Tile.getEnum(tileClicked.getName());

                if (destination == null) {
                    destination = Tile.getEnum(tileClicked.getParent().getName());
                }

                MoveFeedBack status = chess.makeMove(new MoveCommand(selectedPieceTile, destination, null));

                // If the move was authorized
                this.manageAfterMove(status);

                if (status.isStatusOk() && SystemConfig.GAME_TYPE.containsAComputer()) {
                    this.isComputerThinking = true;

                    Thread thread = new Thread(() -> {
                        launchComputerMove();
                        this.isComputerThinking = false;
                    });

                    thread.start();
                }
            } else if (e.getButton() == RIGHT_CLICK) { // No piece selected and we click right (print square in red)
                // color background red
                Tile tileSelected = Tile.getEnum(tileClicked.getName());
                if(tileSelected == null) {
                    tileSelected = Tile.getEnum(tileClicked.getParent().getName());
                }

                if (tileSelected.color() == ch.claudedy.chess.basis.Color.BLACK) {
                    getComponentUI(tileSelected).setBackground(ANALYSE_CLICKED_BLACK_SQUARE);
                } else {
                    getComponentUI(tileSelected).setBackground(ANALYSE_CLICKED_WHITE_SQUARE);
                }
            } else if(e.getButton() == MIDDLE_CLICK) {
                isWhiteView = !isWhiteView;
                this.createSquares();
                this.reset();
            } else { // Reset the board
                this.selectedPieceTile = null;
                this.resetBackgroundTiles();
                this.mouseClicked(e);
            }
        }
    }

    public synchronized void manageAfterMove(MoveFeedBack status) {
        // If the move was authorized
        if (status == MoveFeedBack.RUNNING) { // MOVE OK
            this.reset();
        } else if (status.isGameOver()) { // GAME OVER
            if (SystemConfig.GAME_TYPE.containsAComputer()) {
                stockFish.stopEngine();
            }

            this.reset();
        } else if (status.isStatusError()) { // ERROR FROM MOVE
            this.reset();
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
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
            if (tile.color() == ch.claudedy.chess.basis.Color.BLACK) {
                getComponentUI(tile).setBackground(LEGAL_MOVE_BLACK_SQUARE);
            } else {
                getComponentUI(tile).setBackground(LEGAL_MOVE_WHITE_SQUARE);
            }
        });
    }

    private Color getColorTile(ch.claudedy.chess.basis.Color colorTile) {
        return colorTile.isSameColor(ch.claudedy.chess.basis.Color.BLACK) ? BLACK_SQUARE : WHITE_SQUARE;
    }

    private Color getColorTileAboutMove(ch.claudedy.chess.basis.Color colorTile) {
        return !colorTile.isWhite() ? PREVIOUS_MOVE_BLACK_SQUARE : PREVIOUS_MOVE_WHITE_SQUARE;
    }
}
