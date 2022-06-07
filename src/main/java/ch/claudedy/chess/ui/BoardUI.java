package ch.claudedy.chess.ui;

import ch.claudedy.chess.ApplicationSwing;
import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.systems.SystemConfig;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Accessors(fluent = true)
public class BoardUI extends JPanel {
    private static final int LEFT_CLICK = 1;
    private static final int RIGHT_CLICK = 3;

    private static final java.awt.Color THREATED_BLACK_SQUARE = new java.awt.Color(200, 100, 90);
    private static final java.awt.Color THREATED_WHITE_SQUARE = new java.awt.Color(235, 125, 100);

    private static final java.awt.Color THREATED_KING_BLACK_SQUARE = new java.awt.Color(255, 80, 70);
    private static final java.awt.Color THREATED_KING_WHITE_SQUARE = new java.awt.Color(255, 115, 90);

    private static final java.awt.Color PREVIOUS_MOVE_BLACK_SQUARE = new java.awt.Color(100, 180, 100);
    private static final java.awt.Color PREVIOUS_MOVE_WHITE_SQUARE = new java.awt.Color(130, 200, 130);

    private static final java.awt.Color SELECTED_PIECE_BLACK_SQUARE = new java.awt.Color(100, 155, 180);
    private static final java.awt.Color SELECTED_PIECE_WHITE_SQUARE = new java.awt.Color(130, 170, 200);

    private static final java.awt.Color LEGAL_MOVE_BLACK_SQUARE = new java.awt.Color(240, 240, 80, 255);
    private static final java.awt.Color LEGAL_MOVE_WHITE_SQUARE = new java.awt.Color(255, 255, 150);

    private final Map<String, Integer> squaresBoardUI = new HashMap<>();
    private final ApplicationSwing app;
    private Tile selectedPieceTile;

    public BoardUI(ApplicationSwing application) {
        this.setName("BOARD");
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(600, 600));
        this.setBounds(new Rectangle(0, 50, 600, 600));
        this.app = application;
        this.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (e == null || app.chess().gameStatus().isGameOver()) {
                    return;
                }

                Component tileClicked = getTileUI(e.getX(), e.getY());
                int buttonClicked = e.getButton();

                if (tileClicked != null) {
                    if (selectedPieceTile == null && buttonClicked == LEFT_CLICK && !app.isComputerThinking()) { // No piece selected and we click left on board (select a piece)

                        if (tileClicked instanceof JPanel) {
                            resetBackgroundTiles();
                            return;
                        }

                        selectedPieceTile = Tile.getEnum(tileClicked.getParent().getName());

                        if (selectedPieceTile == null) {
                            selectedPieceTile = Tile.getEnum(tileClicked.getName());
                        }

                        getComponentUI(selectedPieceTile).changeBackground(getColorTileForSelectedPiece(selectedPieceTile.color()));

                        colorizeLegalMoves(app.chess().getLegalMoves(selectedPieceTile));
                    } else if (selectedPieceTile != null && buttonClicked == LEFT_CLICK && !app.isComputerThinking()) { // Piece already selected and we click left on board again (make move)
                        Tile destination = Tile.getEnum(tileClicked.getName());

                        if (destination == null) {
                            destination = Tile.getEnum(tileClicked.getParent().getName());
                        }

                        MoveStatus status = app.chess().makeMove(new MoveCommand(selectedPieceTile, destination, null));

                        // If the move was authorized
                        app.manageAfterMove(status);

                        if (status.isOk() && SystemConfig.GAME_TYPE.containsInLessAComputer()) {
                            app.launchComputerMove();
                        }
                    } else if (e.getButton() == RIGHT_CLICK) { // No piece selected and we click right (print square in red)
                        // color background red
                        Tile tileSelected = Tile.getEnum(tileClicked.getName());
                        if (tileSelected == null) {
                            tileSelected = Tile.getEnum(tileClicked.getParent().getName());
                        }

                        getComponentUI(tileSelected).clickForAnalyse();
                    } else { // Reset the board
                        selectedPieceTile = null;
                        resetBackgroundTiles();
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e == null || app.chess().gameStatus().isGameOver()) {
                    return;
                }

                Component tileClicked = getTileUI(e.getX(), e.getY());
                int buttonClicked = e.getButton();

                if (tileClicked != null) {
                    if (selectedPieceTile == null && buttonClicked == LEFT_CLICK && !app.isComputerThinking()) { // No piece selected and we click left on board (select a piece)

                        if (tileClicked instanceof JPanel) {
                            resetBackgroundTiles();
                            return;
                        }

                        selectedPieceTile = Tile.getEnum(tileClicked.getParent().getName());

                        if (selectedPieceTile == null) {
                            selectedPieceTile = Tile.getEnum(tileClicked.getName());
                        }

                        getComponentUI(selectedPieceTile).changeBackground(getColorTileForSelectedPiece(selectedPieceTile.color()));

                        colorizeLegalMoves(app.chess().getLegalMoves(selectedPieceTile));
                    }
                } else if (e.getButton() == RIGHT_CLICK) { // No piece selected and we click right (print square in red)
                    // color background red
                    Tile tileSelected = Tile.getEnum(tileClicked.getName());
                    if (tileSelected == null) {
                        tileSelected = Tile.getEnum(tileClicked.getParent().getName());
                    }

                    getComponentUI(tileSelected).clickForAnalyse();
                }
            }


            public void mouseReleased(MouseEvent e) {
                if (e == null || app.chess().gameStatus().isGameOver()) {
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

                        MoveStatus status = app.chess().makeMove(new MoveCommand(selectedPieceTile, destination, null));

                        // If the move was authorized
                        app.manageAfterMove(status);

                        if (status.isOk() && SystemConfig.GAME_TYPE.containsInLessAComputer()) {
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
        showKingChecked();

        repaint();
    }

    private void showKingChecked() {
        Tile tileWhiteKing = app.chess().currentBoard().getTileKing(Color.WHITE);
        if (app.chess().currentBoard().isTileChecked(Color.WHITE, tileWhiteKing)) {
            if (tileWhiteKing.color() == Color.BLACK) {
                getComponentUI(tileWhiteKing).changeBackground(THREATED_KING_BLACK_SQUARE);
            } else {
                getComponentUI(tileWhiteKing).changeBackground(THREATED_KING_WHITE_SQUARE);
            }
        }
        Tile tileBlackKing = app.chess().currentBoard().getTileKing(Color.BLACK);
        if (app.chess().currentBoard().isTileChecked(Color.BLACK, tileBlackKing)) {
            if (tileWhiteKing.color() == Color.BLACK) {
                getComponentUI(tileBlackKing).changeBackground(THREATED_KING_BLACK_SQUARE);
            } else {
                getComponentUI(tileBlackKing).changeBackground(THREATED_KING_WHITE_SQUARE);
            }
        }
    }

    public void createSquare(Square squareChess, Integer counter) {
        SquareUI squareUI = new SquareUI(squareChess.tile());
        this.add(squareUI, counter);
        squaresBoardUI.put(squareUI.getName(), counter);
    }


    private synchronized void initSelectedPieceTile() {
        if (selectedPieceTile != null) {
            // Reset color for the selected piece tile
            getComponentUI(selectedPieceTile).resetColor();
        }

        selectedPieceTile = null;
    }

    private synchronized void resetBackgroundTiles() {
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                Tile currentTile = app.chess().currentBoard().squares()[x][y].tile();
                getComponentUI(currentTile).resetColor();
            }
        }
        this.printPreviousMove(app.chess().actualMove());
    }


    private synchronized void printPreviousMove(MoveCommand move) {

        // Reset background previous move
        if (app.chess().actualMove() != null) {
            getComponentUI(app.chess().actualMove().startPosition()).resetColor();
            getComponentUI(app.chess().actualMove().endPosition()).resetColor();
        }

        // Colorize the new move backgrounds
        if (move != null) {
            getComponentUI(move.startPosition()).changeBackground(getColorTileForPreviousMove(move.startPosition().color()));
            getComponentUI(move.endPosition()).changeBackground(getColorTileForPreviousMove(move.endPosition().color()));
        }
    }

    private synchronized void printPieces() {
        Square[][] squares = app.chess().currentBoard().squares();

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                Square currentSquare = squares[x][y];
                getComponentUI(currentSquare.tile()).updatePiece(currentSquare.piece());
            }
        }
    }

    private Component getTileUI(int x, int y) {
        return findComponentAt(x, y);
    }

    private SquareUI getComponentUI(Tile tile) {
        return (SquareUI) getComponent(this.squaresBoardUI.get(tile.name()));
    }

    private void colorizeLegalMoves(List<PossibleMove> possibleMoves) {
        possibleMoves.forEach(move -> {
            if (move.destination().color() == Color.BLACK) {
                if (move.type() == MoveType.THREAT || move.type() == MoveType.EN_PASSANT) {
                    getComponentUI(move.destination()).changeBackground(THREATED_BLACK_SQUARE);
                } else {
                    getComponentUI(move.destination()).changeBackground(LEGAL_MOVE_BLACK_SQUARE);
                }
            } else {
                if (move.type() == MoveType.THREAT || move.type() == MoveType.EN_PASSANT) {
                    getComponentUI(move.destination()).changeBackground(THREATED_WHITE_SQUARE);
                } else {
                    getComponentUI(move.destination()).changeBackground(LEGAL_MOVE_WHITE_SQUARE);
                }
            }
        });
    }

    private java.awt.Color getColorTileForSelectedPiece(Color colorTile) {
        return !colorTile.isWhite() ? SELECTED_PIECE_BLACK_SQUARE : SELECTED_PIECE_WHITE_SQUARE;
    }

    private java.awt.Color getColorTileForPreviousMove(Color colorTile) {
        return !colorTile.isWhite() ? PREVIOUS_MOVE_BLACK_SQUARE : PREVIOUS_MOVE_WHITE_SQUARE;
    }

}
