package model.chess;

import model.chess.actions.MoveCommand;
import model.chess.basis.Chess;
import model.chess.basis.Square;
import model.chess.basis.Tile;
import model.chess.feedbacks.MoveFeedBack;
import model.chess.systems.DataForLoadingBoard;
import model.chess.systems.LoaderFromFile;
import model.chess.utils.FenUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationSwing extends JFrame implements MouseListener, MouseMotionListener {

    private static final Color BLACK_POSITION = new Color(84, 206, 191);
    private static final Color WHITE_POSITION = new Color(122, 230, 234);

    private static final Color SQUARE_BLACK = new Color(62, 118, 168);
    private static final Color SQUARE_WHITE = new Color(121, 173, 208, 255);
    private static final Color SQUARE_RED_DARK = new Color(175, 90, 90);
    private static final Color SQUARE_RED_NORMAL = new Color(200, 100, 100);
    private static final Color SQUARE_YELLOW_DARK = new Color(200, 200, 50);
    private static final Color SQUARE_YELLOW_NORMAL = new Color(255, 255, 100);

    // CHESS (TRUTH)
    private Chess chess;

    // VIEWS (ONLY VIEW PURPOSE)
    private final JLayeredPane layeredPane;
    private JPanel chessBoard;
    private Tile from;
    private Map<String, Tile> piecesView = new HashMap<>();
    private Map<Tile, Integer> squaresView = new HashMap<>();
    private Map<String, Tile> squaresAtView = new HashMap<>();

    public static void main(String[] args) {
        ApplicationSwing frame = new ApplicationSwing();
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public ApplicationSwing() {
        Dimension boardSize = new Dimension(600, 600);

        //  Use a Layered Pane for this application
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(boardSize);
        layeredPane.addMouseListener(this);
        layeredPane.addMouseMotionListener(this);
        getContentPane().add(layeredPane);

        this.startGame();
    }

    private void startGame() {
        Dimension boardSize = new Dimension(600, 600);

        if (this.layeredPane.getComponentCount() != 0) {
            this.layeredPane.removeAll();
        }

        //Add a chess board to the Layered Pane
        chessBoard = new JPanel();
        chessBoard.setLayout(new GridLayout(8, 8));
        chessBoard.setPreferredSize(boardSize);
        chessBoard.setBounds(0, 0, boardSize.width, boardSize.height);
        layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);

        DataForLoadingBoard loadingBoard = LoaderFromFile.readFile("fen/board-default.csv");
        chess = new Chess(loadingBoard.fen(), loadingBoard.previousMove());

        this.resetVariables();
        this.placeSquare();
        this.resetBackgroundTiles();
        this.placePieces();
        this.printPreviousMove(chess.actualMove());

        System.out.println(FenUtils.boardToFen(chess.currentBoard()));
    }

    private void placeSquare() {
        piecesView = new HashMap<>();
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

    private void placePieces() {
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

    private void resetVariables() {
        if (from != null)
            chessBoard.getComponent(squaresView.get(from)).setBackground(getColorTile(from.color()));

        from = null;
        piecesView = new HashMap<>();
    }

    private void resetBackgroundTiles() {
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                Tile currentTile = chess.currentBoard().squares()[x][y].tile();
                chessBoard.getComponent(squaresView.get(currentTile)).setBackground(getColorTile(currentTile.color()));
            }
        }
        this.printPreviousMove(chess.actualMove());
    }

    private void reset() {
        this.resetVariables();
        this.placePieces();
        this.resetBackgroundTiles();
    }

    private void printPreviousMove(MoveCommand move) {

        // Reset background previous move
        if (chess.actualMove() != null) {
            chessBoard.getComponent(squaresView.get(chess.actualMove().startPosition())).setBackground(getColorTile(chess.actualMove().startPosition().color()));
            chessBoard.getComponent(squaresView.get(chess.actualMove().endPosition())).setBackground(getColorTile(chess.actualMove().endPosition().color()));
        }

        // Colorize the new move backgrounds
        if (move != null) {
            chessBoard.getComponent(squaresView.get(move.startPosition())).setBackground(getColorTilePosition(move.startPosition().color()));
            chessBoard.getComponent(squaresView.get(move.endPosition())).setBackground(getColorTilePosition(move.endPosition().color()));
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (from == null && e.getButton() == 1) {
            this.resetBackgroundTiles();
            Component c = chessBoard.findComponentAt(e.getX(), e.getY());

            if (c instanceof JPanel) {
                return;
            }

            from = piecesView.get(c.getParent().getName());
            chessBoard.getComponent(squaresView.get(from)).setBackground(getColorTilePosition(from.color()));

            this.colorizeLegalMoves(chess.getLegalMoves(from));
        } else if (from != null && e.getButton() == 1) {
            Component c = chessBoard.findComponentAt(e.getX(), e.getY());
            Tile destination = squaresAtView.get(c.getName());
            if (destination == null) {
                destination = squaresAtView.get(c.getParent().getName());
            }

            MoveCommand moveCommand = new MoveCommand(from, destination);
            MoveFeedBack moveFeedBack = chess.makeMove(moveCommand);

            if (moveFeedBack == MoveFeedBack.AUTHORIZED) {
                this.reset();
                this.printPreviousMove(moveCommand);
            } else {
                this.reset();
                System.out.println(moveFeedBack);
                this.mouseClicked(e);
            }
        } else if (from == null && e.getButton() == 3) {
            // color background red
            Component c = chessBoard.findComponentAt(e.getX(), e.getY());
            Tile tileSelected = squaresAtView.get(c.getName());
            if (tileSelected == null) {
                tileSelected = squaresAtView.get(c.getParent().getName());
            }

            if (tileSelected.color() == model.chess.basis.Color.BLACK) {
                chessBoard.getComponent(squaresView.get(tileSelected)).setBackground(SQUARE_RED_DARK);
            } else {
                chessBoard.getComponent(squaresView.get(tileSelected)).setBackground(SQUARE_RED_NORMAL);
            }
        } else if (from == null && e.getButton() == 2) {
            chess.rollbackPreviousState();
            this.reset();
        } else {
            this.from = null;
            this.resetBackgroundTiles();
            this.mouseClicked(e);
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

    private void colorizeLegalMoves(List<Tile> tiles) {
        tiles.forEach(tile -> {
            if (tile.color() == model.chess.basis.Color.BLACK) {
                chessBoard.getComponent(squaresView.get(tile)).setBackground(SQUARE_YELLOW_DARK);
            } else {
                chessBoard.getComponent(squaresView.get(tile)).setBackground(SQUARE_YELLOW_NORMAL);
            }
        });
    }

    private Color getColorTile(model.chess.basis.Color colorTile) {
        return colorTile.isSameColor(model.chess.basis.Color.BLACK) ? SQUARE_BLACK : SQUARE_WHITE;
    }

    private Color getColorTilePosition(model.chess.basis.Color colorTile) {
        return colorTile.isSameColor(model.chess.basis.Color.BLACK) ? BLACK_POSITION : WHITE_POSITION;
    }
}
