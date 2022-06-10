package ch.claudedy.chess.ui;

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
    private static final java.awt.Color THREATED_WHITE_SQUARE = new java.awt.Color(215, 105, 95);

    private static final java.awt.Color THREATED_KING_BLACK_SQUARE = new java.awt.Color(255, 80, 70);
    private static final java.awt.Color THREATED_KING_WHITE_SQUARE = new java.awt.Color(255, 90, 80);

    private static final java.awt.Color PREVIOUS_MOVE_BLACK_SQUARE = new java.awt.Color(100, 180, 100);
    private static final java.awt.Color PREVIOUS_MOVE_WHITE_SQUARE = new java.awt.Color(130, 200, 130);

    private static final java.awt.Color SELECTED_PIECE_BLACK_SQUARE = new java.awt.Color(150, 150, 150, 255);
    private static final java.awt.Color SELECTED_PIECE_WHITE_SQUARE = new java.awt.Color(220, 220, 220);

    private static final java.awt.Color LEGAL_MOVE_BLACK_SQUARE = new java.awt.Color(240, 240, 80, 255);
    private static final java.awt.Color LEGAL_MOVE_WHITE_SQUARE = new java.awt.Color(255, 255, 150);

    private final Map<String, Integer> squaresBoardUI = new HashMap<>();
    private final ChessUI chessUI;
    private Tile selectedPieceTile;

    public BoardUI(ChessUI chessUI) {
        this.chessUI = chessUI;

        this.setName("BOARD");
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(600, 600));
        this.setBounds(new Rectangle(0, 50, 600, 600));
        this.addMouseListener(new MouseListener() {

            // MOUSE CLICKED
            public void mouseClicked(MouseEvent e) {
                if (e == null || chessUI.chess().gameStatus().isGameOver()) {
                    return;
                }

                // Search the clicked tile on the board itself
                Component tileClicked = getTileUI(e.getX(), e.getY());
                int buttonClicked = e.getButton();

                if (tileClicked != null) {
                    if (selectedPieceTile == null && buttonClicked == LEFT_CLICK && !chessUI.isComputerThinking()) { // There is no selected piece and we click left on board (select a piece)
                        // If the click was on a empty tile (cause piece is a JLabel)
                        if (tileClicked instanceof JPanel) {
                            resetBackgroundTiles();
                            return;
                        }

                        // We take the parent name (the tile of the piece)
                        selectedPieceTile = Tile.getEnum(tileClicked.getParent().getName());

                        if (selectedPieceTile == null) {
                            selectedPieceTile = Tile.getEnum(tileClicked.getName());
                        }

                        // Color the tile of the piece
                        getComponentUI(selectedPieceTile).changeBackground(getColorTileForSelectedPiece(selectedPieceTile.color()));

                        // Show the legal moves for the selected piece
                        colorizeLegalMoves(chessUI.chess().getLegalMoves(selectedPieceTile));
                    } else if (selectedPieceTile != null && buttonClicked == LEFT_CLICK && !chessUI.isComputerThinking()) { // There is already a selected piece and we click left on board again (make move)
                        Tile destination = Tile.getEnum(tileClicked.getName());

                        if (destination == null) {
                            destination = Tile.getEnum(tileClicked.getParent().getName());
                        }

                        // Make the move
                        MoveStatus status = chessUI.chess().makeMove(new MoveCommand(selectedPieceTile, destination, null));

                        // Manage the move done
                        chessUI.manageAfterMove(status);

                        // If we play against a computer, we launch the computer move
                        if (status.isOk() && SystemConfig.GAME_TYPE.containsInLessAComputer()) {
                            chessUI.launchComputerMove();
                        }
                    } else if (e.getButton() == RIGHT_CLICK) { // There is no selected piece and we click right (print square to analyse board)
                        Tile tileSelected = Tile.getEnum(tileClicked.getName());
                        if (tileSelected == null) {
                            tileSelected = Tile.getEnum(tileClicked.getParent().getName());
                        }

                        // Let's manage the click on the tile (to set a background etc..)
                        getComponentUI(tileSelected).clickForAnalyse();
                    } else { // Reset the board
                        selectedPieceTile = null;
                        resetBackgroundTiles();
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
            }


            public void mouseReleased(MouseEvent e) {
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
        // Search the WHITE KING and if he is in check, we color his tile
        Tile tileWhiteKing = chessUI.chess().currentBoard().getTileKing(Color.WHITE);
        if (chessUI.chess().currentBoard().isTileChecked(Color.WHITE, tileWhiteKing)) {
            if (tileWhiteKing.color() == Color.BLACK) {
                getComponentUI(tileWhiteKing).changeBackground(THREATED_KING_BLACK_SQUARE);
            } else {
                getComponentUI(tileWhiteKing).changeBackground(THREATED_KING_WHITE_SQUARE);
            }
        }

        // Search the BLACK KING and if he is in check, we color his tile
        Tile tileBlackKing = chessUI.chess().currentBoard().getTileKing(Color.BLACK);
        if (chessUI.chess().currentBoard().isTileChecked(Color.BLACK, tileBlackKing)) {
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
                Tile currentTile = chessUI.chess().currentBoard().squares()[x][y].tile();
                getComponentUI(currentTile).resetColor();
            }
        }

        this.printPreviousMove(chessUI.chess().actualMove());
    }


    private synchronized void printPreviousMove(MoveCommand move) {

        // Reset background previous move
        if (chessUI.chess().actualMove() != null) {
            getComponentUI(chessUI.chess().actualMove().startPosition()).resetColor();
            getComponentUI(chessUI.chess().actualMove().endPosition()).resetColor();
        }

        // Colorize the new move backgrounds
        if (move != null) {
            getComponentUI(move.startPosition()).changeBackground(getColorTileForPreviousMove(move.startPosition().color()));
            getComponentUI(move.endPosition()).changeBackground(getColorTileForPreviousMove(move.endPosition().color()));
        }
    }

    private synchronized void printPieces() {
        Square[][] squares = chessUI.chess().currentBoard().squares();

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
