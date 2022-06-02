package ch.claudedy.chess;

import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.systems.*;
import ch.claudedy.chess.utils.FenUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationSwing extends JFrame implements MouseListener, MouseMotionListener {

    private static final Color DARK_GREEN_MOVE = new Color(100, 180, 100);
    private static final Color NORMAL_GREEN_MOVE = new Color(120, 200, 120);

    private static final Color SQUARE_BLACK = new Color(75, 115, 145);
    private static final Color SQUARE_WHITE = new Color(230, 230, 210);

    private static final Color SQUARE_RED_DARK = new Color(200, 100, 90);
    private static final Color SQUARE_RED_NORMAL = new Color(235, 125, 100);
    private static final Color SQUARE_YELLOW_DARK = new Color(180, 123, 100);
    private static final Color SQUARE_YELLOW_NORMAL = new Color(240, 192, 180);

    private static final int LEFT_CLICK = 1;
    private static final int RIGHT_CLICK = 3;
    private static final int MIDDLE_CLICK = 2;

    private StockFish stockFish;

    // CHESS (TRUTH)
    private Chess chess;

    // VIEWS (ONLY VIEW PURPOSE)
    private final JLayeredPane layeredPane;
    private JPanel chessBoard;
    private JPanel informationArea;

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
            String bestMove = stockFish.getBestMove(FenUtils.boardToFen(chess.currentBoard()), 1000);
            System.out.println("The computer's move : " + bestMove);

            MoveFeedBack status = chess.makeMove(MoveCommand.convert(bestMove));
            this.manageAfterMove(status);
        } else if (SystemConfig.GAME_TYPE == GameType.COMPUTER_V_COMPUTER) {

            Thread thread = new Thread(() -> {
                int counter = 0;
                MoveFeedBack status = MoveFeedBack.RUNNING;
                while (counter <= 100 && status == MoveFeedBack.RUNNING) {
                    String bestMove = stockFish.getBestMove(FenUtils.boardToFen(chess.currentBoard()), 1000);
                    System.out.println("The computer's move : " + bestMove);

                    status = chess.makeMove(MoveCommand.convert(bestMove));
                    manageAfterMove(status);

                    counter++;
                }

                Thread.currentThread().interrupt();
            });

            thread.start();
        }
    }

    private synchronized void printInformationArea() {
        if (informationArea.getComponentCount() != 0) {
            informationArea.removeAll();
        }

        Board currentBoard = chess.currentBoard();

        JTextField panelCurrentPlayer = new JTextField("Current Player : " + currentBoard.currentPlayer());
        panelCurrentPlayer.setName("CURRENT_PLAYER");
        informationArea.add(panelCurrentPlayer);

        JTextField panelFen = new JTextField("Fen : " + FenUtils.boardToFen(currentBoard));
        panelFen.setName("FEN");
        informationArea.add(panelFen);

        JTextField panelGameStatus = new JTextField("Status : " + chess.status());
        panelGameStatus.setName("GAME_STATUS");
        informationArea.add(panelGameStatus);
    }

    private synchronized void createSquares() {
        chessBoard.removeAll();

        Square[][] squares = chess.currentBoard().squares();

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                JPanel squarePanel = new JPanel(new BorderLayout());
                Square currentSquare = squares[x][y];
                squarePanel.setName(currentSquare.tile().name());
                chessBoard.add(squarePanel);
            }
        }
    }

    private synchronized void printPieces() {
        Square[][] squares = chess.currentBoard().squares();

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {

                Square currentSquare = squares[x][y];
                Component component = Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(currentSquare.tile().name())).findFirst().get();
                if (component instanceof JPanel) {
                    JPanel panel = (JPanel) component;

                    if (panel.getComponentCount() != 0) {
                        Arrays.stream(panel.getComponents()).forEach(comp -> {
                            comp.setVisible(false);
                        });
                    }

                    if (currentSquare.piece() != null) {
                        String namePiece = currentSquare.piece().type().getAbrevTechnicalBlack() + "_" + currentSquare.piece().color();
                        URL systemResource = ClassLoader.getSystemResource("images/" + namePiece + ".png");
                        JLabel piece = new JLabel(new ImageIcon(systemResource));
                        panel.add(piece);
                        panel.setFocusable(false);
                    } else {
                        panel.setFocusable(true);
                    }
                }
            }
        }
    }

    private synchronized void initSelectedPieceTile() {
        if (selectedPieceTile != null) {
            // Reset color for the selected piece tile
            Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(selectedPieceTile.name())).findFirst().get().setBackground(getColorTile(selectedPieceTile.color()));
        }

        selectedPieceTile = null;
    }

    private synchronized void resetBackgroundTiles() {
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                Tile currentTile = chess.currentBoard().squares()[x][y].tile();
                Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(currentTile.name())).findFirst().get().setBackground(getColorTile(currentTile.color()));
            }
        }
        this.printPreviousMove(chess.actualMove());
    }

    private synchronized void reset() {
        this.initSelectedPieceTile();
        this.printInformationArea();
        this.printPieces();
        this.resetBackgroundTiles();
    }

    private synchronized void printPreviousMove(MoveCommand move) {

        // Reset background previous move
        if (chess.actualMove() != null) {
            Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(chess.actualMove().startPosition().name())).findFirst().get().setBackground(getColorTile(chess.actualMove().startPosition().color()));
            Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(chess.actualMove().endPosition().name())).findFirst().get().setBackground(getColorTile(chess.actualMove().endPosition().color()));
        }

        // Colorize the new move backgrounds
        if (move != null) {
            Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(move.startPosition().name())).findFirst().get().setBackground(getColorTileAboutMove(move.startPosition().color()));
            Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(move.endPosition().name())).findFirst().get().setBackground(getColorTileAboutMove(move.endPosition().color()));
        }
    }

    public void mouseClicked(MouseEvent e) {
        Component tileClicked = getTileUI(e.getX(), e.getY());
        int buttonClicked = e.getButton();

        if (tileClicked != null) {
            if (selectedPieceTile == null && buttonClicked == LEFT_CLICK) { // No piece selected and we click left on board (select a piece)
                this.resetBackgroundTiles();

                if (tileClicked instanceof JPanel) {
                    return;
                }

                selectedPieceTile = Tile.getEnum(tileClicked.getParent().getName());

                if (selectedPieceTile == null) {
                    selectedPieceTile = Tile.getEnum(tileClicked.getName());
                }

                Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(selectedPieceTile.name())).findFirst().get().setBackground(getColorTileAboutMove(selectedPieceTile.color()));

                this.colorizeLegalMoves(chess.getLegalMoves(selectedPieceTile));
            } else if (selectedPieceTile != null && buttonClicked == LEFT_CLICK) { // Piece already selected and we click left on board again (make move)
                Tile destination = Tile.getEnum(tileClicked.getName());

                if (destination == null) {
                    destination = Tile.getEnum(tileClicked.getParent().getName());
                }

                MoveFeedBack status = chess.makeMove(new MoveCommand(selectedPieceTile, destination, null));

                // If the move was authorized
                this.manageAfterMove(status);

                if (status.isStatusOk() && SystemConfig.GAME_TYPE.containsAComputer()) {
                    String bestMove = stockFish.getBestMove(FenUtils.boardToFen(chess.currentBoard()), 1000);
                    System.out.println("The computer's move : " + bestMove);

                    status = chess.makeMove(MoveCommand.convert(bestMove));
                    this.manageAfterMove(status);
                }
            } else if (selectedPieceTile == null && e.getButton() == RIGHT_CLICK) { // No piece selected and we click right (print square in red)
                // color background red
                final Tile tileSelected = tileClicked.getName() != null ? Tile.getEnum(tileClicked.getName()) : Tile.getEnum(tileClicked.getParent().getName());

                if (tileSelected.color() == ch.claudedy.chess.basis.Color.BLACK) {
                    Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(tileSelected.name())).findFirst().get().setBackground(SQUARE_RED_DARK);
                } else {
                    Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(tileSelected.name())).findFirst().get().setBackground(SQUARE_RED_NORMAL);
                }
            } else if (selectedPieceTile == null && e.getButton() == MIDDLE_CLICK) { // No piece selected and we click middle (rollback last board)
                chess.rollbackPreviousState();
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
            this.startNewGame();
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

    private void colorizeLegalMoves(List<Tile> tiles) {
        tiles.forEach(tile -> {
            if (tile.color() == ch.claudedy.chess.basis.Color.BLACK) {
                Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(tile.name())).findFirst().get().setBackground(SQUARE_YELLOW_DARK);
            } else {
                Arrays.stream(chessBoard.getComponents()).filter(comp -> comp.getName().equals(tile.name())).findFirst().get().setBackground(SQUARE_YELLOW_NORMAL);
            }
        });
    }

    private Color getColorTile(ch.claudedy.chess.basis.Color colorTile) {
        return colorTile.isSameColor(ch.claudedy.chess.basis.Color.BLACK) ? SQUARE_BLACK : SQUARE_WHITE;
    }

    private Color getColorTileAboutMove(ch.claudedy.chess.basis.Color colorTile) {
        return !colorTile.isWhite() ? DARK_GREEN_MOVE : NORMAL_GREEN_MOVE;
    }
}
