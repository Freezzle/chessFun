package ch.claudedy.chess.ui;

import ch.claudedy.chess.ApplicationSwing;
import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.systems.SystemConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessBoard extends JPanel {
    private static final int LEFT_CLICK = 1;
    private static final int RIGHT_CLICK = 3;

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

    private final Map<String, ImageIcon> piecesImages = new HashMap<>();
    private final Map<String, Integer> squaresBoardUI = new HashMap<>();
    private final ApplicationSwing app;
    private Tile selectedPieceTile;

    public ChessBoard(ApplicationSwing application) {
        this.setName("BOARD");
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(600, 600));
        this.setBounds(new Rectangle(0, 50, 600, 600));
        this.app = application;
        this.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (e == null || app.getChess().gameStatus().isGameOver()) {
                    return;
                }

                Component tileClicked = getTileUI(e.getX(), e.getY());
                int buttonClicked = e.getButton();

                if (tileClicked != null) {
                    if (selectedPieceTile == null && buttonClicked == LEFT_CLICK && !app.isComputerThinking()) { // No piece selected and we click left on board (select a piece)
                        resetBackgroundTiles();

                        if (tileClicked instanceof JPanel) {
                            return;
                        }

                        selectedPieceTile = Tile.getEnum(tileClicked.getParent().getName());

                        if (selectedPieceTile == null) {
                            selectedPieceTile = Tile.getEnum(tileClicked.getName());
                        }

                        getComponentUI(selectedPieceTile).setBackground(getColorTileForSelectedPiece(selectedPieceTile.color()));

                        colorizeLegalMoves(app.getChess().getLegalMoves(selectedPieceTile));
                    } else if (selectedPieceTile != null && buttonClicked == LEFT_CLICK && !app.isComputerThinking()) { // Piece already selected and we click left on board again (make move)
                        Tile destination = Tile.getEnum(tileClicked.getName());

                        if (destination == null) {
                            destination = Tile.getEnum(tileClicked.getParent().getName());
                        }

                        MoveStatus status = app.getChess().makeMove(new MoveCommand(selectedPieceTile, destination, null));

                        // If the move was authorized
                        app.manageAfterMove(status);

                        if (status.isOk() && SystemConfig.GAME_TYPE.containsAComputer()) {
                            app.launchComputerMove();
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
                        selectedPieceTile = null;
                        resetBackgroundTiles();
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e == null || app.getChess().gameStatus().isGameOver()) {
                    return;
                }

                Component tileClicked = getTileUI(e.getX(), e.getY());
                int buttonClicked = e.getButton();

                if (tileClicked != null) {
                    if (selectedPieceTile == null && buttonClicked == LEFT_CLICK && !app.isComputerThinking()) { // No piece selected and we click left on board (select a piece)
                        resetBackgroundTiles();

                        if (tileClicked instanceof JPanel) {
                            return;
                        }

                        selectedPieceTile = Tile.getEnum(tileClicked.getParent().getName());

                        if (selectedPieceTile == null) {
                            selectedPieceTile = Tile.getEnum(tileClicked.getName());
                        }

                        getComponentUI(selectedPieceTile).setBackground(getColorTileForSelectedPiece(selectedPieceTile.color()));

                        colorizeLegalMoves(app.getChess().getLegalMoves(selectedPieceTile));
                    }
                }
            }


            public void mouseReleased(MouseEvent e) {
                if (e == null || app.getChess().gameStatus().isGameOver()) {
                    return;
                }

                Component tileClicked = getTileUI(e.getX(), e.getY());
                int buttonClicked = e.getButton();

                if (tileClicked != null) {
                    if (selectedPieceTile != null && buttonClicked == LEFT_CLICK && !app.isComputerThinking()) { // Piece already selected and we click left on board again (make move)
                        Tile destination = Tile.getEnum(tileClicked.getName());

                        if (destination == null) {
                            destination = Tile.getEnum(tileClicked.getParent().getName());
                        }

                        MoveStatus status = app.getChess().makeMove(new MoveCommand(selectedPieceTile, destination, null));

                        // If the move was authorized
                        app.manageAfterMove(status);

                        if (status.isOk() && SystemConfig.GAME_TYPE.containsAComputer()) {
                            app.launchComputerMove();
                        }
                    }
                }
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
    }

    public void resetBoard() {
        initSelectedPieceTile();
        resetBackgroundTiles();
        printPieces();

        doLayout();
    }

    public void addSquare(JPanel square, Integer counter) {
        this.add(square, counter);
        squaresBoardUI.put(square.getName(), counter);
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
                Tile currentTile = app.getChess().currentBoard().squares()[x][y].tile();
                getComponentUI(currentTile).setBackground(getColorTile(currentTile.color()));
            }
        }
        this.printPreviousMove(app.getChess().actualMove());
    }


    private synchronized void printPreviousMove(MoveCommand move) {

        // Reset background previous move
        if (app.getChess().actualMove() != null) {
            getComponentUI(app.getChess().actualMove().startPosition()).setBackground(getColorTile(app.getChess().actualMove().startPosition().color()));
            getComponentUI(app.getChess().actualMove().endPosition()).setBackground(getColorTile(app.getChess().actualMove().endPosition().color()));
        }

        // Colorize the new move backgrounds
        if (move != null) {
            getComponentUI(move.startPosition()).setBackground(getColorTileForPreviousMove(move.startPosition().color()));
            getComponentUI(move.endPosition()).setBackground(getColorTileForPreviousMove(move.endPosition().color()));
        }
    }

    private synchronized void printPieces() {
        Square[][] squares = app.getChess().currentBoard().squares();

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

    private Component getTileUI(int x, int y) {
        return findComponentAt(x, y);
    }

    private Component getComponentUI(Tile tile) {
        return getComponent(this.squaresBoardUI.get(tile.name()));
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

    private java.awt.Color getColorTileForSelectedPiece(Color colorTile) {
        return !colorTile.isWhite() ? SELECTED_PIECE_BLACK_SQUARE : SELECTED_PIECE_WHITE_SQUARE;
    }

    private java.awt.Color getColorTile(Color colorTile) {
        return colorTile.isSameColor(Color.BLACK) ? BLACK_SQUARE : WHITE_SQUARE;
    }

    private java.awt.Color getColorTileForPreviousMove(Color colorTile) {
        return !colorTile.isWhite() ? PREVIOUS_MOVE_BLACK_SQUARE : PREVIOUS_MOVE_WHITE_SQUARE;
    }

}
