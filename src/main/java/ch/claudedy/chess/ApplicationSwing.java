package ch.claudedy.chess;

import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.systems.*;
import ch.claudedy.chess.utils.FenUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationSwing extends JFrame implements MouseListener, MouseMotionListener {

    private static final Color DARK_GREEN_MOVE = new Color(100, 180, 100);
    private static final Color NORMAL_GREEN_MOVE = new Color(120, 200, 120);

    private static final Color DARK_ORANGE_MOVE = new Color(175, 100, 180);
    private static final Color NORMAL_ORANGE_MOVE = new Color(200, 120, 183);

    private static final Color SQUARE_BLACK = new Color(180, 140, 100);
    private static final Color SQUARE_WHITE = new Color(240, 220, 180);

    private static final Color SQUARE_RED_DARK = new Color(175, 90, 90);
    private static final Color SQUARE_RED_NORMAL = new Color(200, 100, 100);
    private static final Color SQUARE_YELLOW_DARK = new Color(180, 123, 100);
    private static final Color SQUARE_YELLOW_NORMAL = new Color(240, 192, 180);

    private static final int LEFT_CLICK = 1;
    private static final int RIGHT_CLICK = 3;
    private static final int MIDDLE_CLICK = 2;

    private static StockFish stockFish;

    // CHESS (TRUTH)
    private Chess chess;

    // VIEWS (ONLY VIEW PURPOSE)
    private final JLayeredPane layeredPane;
    private JPanel chessBoard;
    private JPanel informationArea;
    private Map<String, Tile> piecesView = new HashMap<>();
    private Map<Tile, Integer> squaresView = new HashMap<>();
    private Map<String, Tile> squaresAtView = new HashMap<>();

    // CHOICE ABOUT PLAYER
    private Tile selectedPieceTile;

    public static void main(String[] args) {
        if (SystemConfig.COMPUTER_ON) {
            stockFish = new StockFish();
        }

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

    private void startNewGame() {
        if (SystemConfig.COMPUTER_ON) {
            stockFish.startEngine();
        }

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

        this.printSquares();
        this.reset();
        this.printPreviousMove(chess.actualMove(), chess.currentBoard().currentPlayer().reverseColor());
    }

    private void printInformationArea() {

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

    private void printSquares() {
        squaresAtView = new HashMap<>();
        squaresView = new HashMap<>();
        chessBoard.removeAll();

        int counter = 0;
        Square[][] squares = chess.currentBoard().squares();

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                JPanel square = new JPanel(new BorderLayout());
                square.setName(counter + "");
                Square currentSquare = squares[x][y];

                squaresAtView.put(square.getName(), currentSquare.tile());
                squaresView.put(currentSquare.tile(), counter);
                chessBoard.add(square);

                counter++;
            }
        }
    }

    private void printPieces() {
        piecesView = new HashMap<>();

        int counter = 0;
        Square[][] squares = chess.currentBoard().squares();

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {

                Square currentSquare = squares[x][y];
                if (chessBoard.getComponent(counter) instanceof JPanel) {
                    JPanel panel = (JPanel) chessBoard.getComponent(counter);

                    if (panel.getComponentCount() != 0) {
                        panel.getComponent(0).setVisible(false);
                        panel.remove(0);
                    }

                    if (currentSquare.piece() != null) {
                        String namePiece = currentSquare.piece().type().getAbrevTechnicalBlack() + "_" + currentSquare.piece().color();
                        URL systemResource = ClassLoader.getSystemResource("images/" + namePiece + ".png");

                        JLabel piece = new JLabel(new ImageIcon(systemResource));
                        piece.setName(counter + "");
                        panel.setName(counter + "");
                        panel.add(piece);
                        panel.setFocusable(false);
                        piecesView.put(panel.getName(), currentSquare.tile());
                    } else {
                        panel.setFocusable(true);
                    }
                }

                counter++;
            }
        }
    }

    private void initSelectedPieceTile() {
        if (selectedPieceTile != null) {
            // Reset color for the selected piece tile
            chessBoard.getComponent(squaresView.get(selectedPieceTile)).setBackground(getColorTile(selectedPieceTile.color()));
        }

        selectedPieceTile = null;
    }

    private void resetBackgroundTiles() {
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                Tile currentTile = chess.currentBoard().squares()[x][y].tile();
                chessBoard.getComponent(squaresView.get(currentTile)).setBackground(getColorTile(currentTile.color()));
            }
        }
        this.printPreviousMove(chess.actualMove(), chess.currentBoard().currentPlayer().reverseColor());
    }

    private void reset() {
        this.initSelectedPieceTile();
        this.printInformationArea();
        this.printPieces();
        this.resetBackgroundTiles();
    }

    private void printPreviousMove(MoveCommand move, ch.claudedy.chess.basis.Color player) {

        // Reset background previous move
        if (chess.actualMove() != null) {
            chessBoard.getComponent(squaresView.get(chess.actualMove().startPosition())).setBackground(getColorTile(chess.actualMove().startPosition().color()));
            chessBoard.getComponent(squaresView.get(chess.actualMove().endPosition())).setBackground(getColorTile(chess.actualMove().endPosition().color()));
        }

        // Colorize the new move backgrounds
        if (move != null) {
            chessBoard.getComponent(squaresView.get(move.startPosition())).setBackground(getColorTileAboutMove(move.startPosition().color(), player));
            chessBoard.getComponent(squaresView.get(move.endPosition())).setBackground(getColorTileAboutMove(move.endPosition().color(), player));
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

                selectedPieceTile = piecesView.get(tileClicked.getParent().getName());
                chessBoard.getComponent(squaresView.get(selectedPieceTile)).setBackground(getColorTileAboutMove(selectedPieceTile.color(), chess.currentBoard().currentPlayer()));

                this.colorizeLegalMoves(chess.getLegalMoves(selectedPieceTile));
            } else if (selectedPieceTile != null && buttonClicked == LEFT_CLICK) { // Piece already selected and we click left on board again (make move)
                Tile destination = squaresAtView.get(tileClicked.getName());
                if (destination == null) {
                    destination = squaresAtView.get(tileClicked.getParent().getName());
                }

                MoveCommand moveCommand = new MoveCommand(selectedPieceTile, destination, null);
                MoveFeedBack status = chess.makeMove(moveCommand);

                // If the move was authorized
                if (status == MoveFeedBack.RUNNING) {
                    this.reset();
                    this.printPreviousMove(moveCommand, chess.currentBoard().currentPlayer().reverseColor());

                    if (SystemConfig.COMPUTER_ON) {
                        String bestMove = stockFish.getBestMove(FenUtils.boardToFen(chess.currentBoard()), 1000);
                        System.out.println("The computer's move : " + bestMove);
                        MoveCommand commandComputer = MoveCommand.convert(bestMove);
                        status = chess.makeMove(commandComputer);

                        this.reset();
                        this.printPreviousMove(commandComputer, chess.currentBoard().currentPlayer().reverseColor());
                    }

                    if (status == MoveFeedBack.CHECKMATED || status == MoveFeedBack.STALEMATED || status == MoveFeedBack.RULES_50) {
                        if (SystemConfig.COMPUTER_ON) {
                            stockFish.stopEngine();
                        }

                        this.reset();
                        this.startNewGame();
                    }
                } else if (status == MoveFeedBack.CHECKMATED || status == MoveFeedBack.STALEMATED || status == MoveFeedBack.RULES_50) {

                    if (SystemConfig.COMPUTER_ON) {
                        stockFish.stopEngine();
                    }

                    this.reset();
                    this.startNewGame();
                } else {
                    this.reset();
                    this.mouseClicked(e);
                }
            } else if (selectedPieceTile == null && e.getButton() == RIGHT_CLICK) { // No piece selected and we click right (print square in red)
                // color background red
                Tile tileSelected = squaresAtView.get(tileClicked.getName());
                if (tileSelected == null) {
                    tileSelected = squaresAtView.get(tileClicked.getParent().getName());
                }

                if (tileSelected.color() == ch.claudedy.chess.basis.Color.BLACK) {
                    chessBoard.getComponent(squaresView.get(tileSelected)).setBackground(SQUARE_RED_DARK);
                } else {
                    chessBoard.getComponent(squaresView.get(tileSelected)).setBackground(SQUARE_RED_NORMAL);
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
                chessBoard.getComponent(squaresView.get(tile)).setBackground(SQUARE_YELLOW_DARK);
            } else {
                chessBoard.getComponent(squaresView.get(tile)).setBackground(SQUARE_YELLOW_NORMAL);
            }
        });
    }

    private Color getColorTile(ch.claudedy.chess.basis.Color colorTile) {
        return colorTile.isSameColor(ch.claudedy.chess.basis.Color.BLACK) ? SQUARE_BLACK : SQUARE_WHITE;
    }

    private Color getColorTileAboutMove(ch.claudedy.chess.basis.Color colorTile, ch.claudedy.chess.basis.Color player) {
        if (player.isWhite()) {
            return !colorTile.isWhite() ? DARK_GREEN_MOVE : NORMAL_GREEN_MOVE;
        } else {
            return !colorTile.isWhite() ? DARK_ORANGE_MOVE : NORMAL_ORANGE_MOVE;
        }
    }
}
