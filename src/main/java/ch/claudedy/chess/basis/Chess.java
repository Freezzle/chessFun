package ch.claudedy.chess.basis;

import ch.claudedy.chess.systems.ConsolePrint;
import ch.claudedy.chess.systems.SystemConfig;
import ch.claudedy.chess.utils.FenUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class Chess {

    private List<HistoricalBoardFen> historicalBoards;
    private MoveCommand actualMove;
    private Board currentBoard;

    private MoveFeedBack status;

    public Chess(String fen, MoveCommand previousMove) {
        this.currentBoard = FenUtils.fenToBoard(fen);
        this.historicalBoards = new ArrayList<>();
        this.actualMove = previousMove;
        this.status = MoveFeedBack.RUNNING;
    }

    public MoveFeedBack makeMove(MoveCommand move) {
        // Check if the move is authorized
        status = this.isThatMoveLegal(move);

        if (status == MoveFeedBack.RUNNING) {
            // Create a copy of the board before to make action
            this.historicalBoards.add(new HistoricalBoardFen().fen(FenUtils.boardToFen(this.currentBoard)).previousMove(this.actualMove));

            // The move action become the actual move
            this.actualMove = move;

            // Move the piece
            this.currentBoard.movePiece(move.startPosition(), move.endPosition(), move.promote());

            // Switch the player
            this.currentBoard.switchPlayer();

            // Check if the enemy king is checkmated, etc..
            status = this.checkGameStatus();

            if(SystemConfig.PRINT_CONSOLE){
                ConsolePrint.execute(this);
            }
        }

        return status;
    }

    private MoveFeedBack checkGameStatus() {

        if(this.currentBoard.fiftyRules() == 50) {
            return MoveFeedBack.RULES_50;
        }

        // King is checked
        Tile tileKing = this.getTileKing(currentBoard, this.currentBoard.currentPlayer());

        boolean cannotMove = getLegalMoves(tileKing).isEmpty();

        if (!cannotMove) {
            return MoveFeedBack.RUNNING;
        }

        boolean kingChecked = this.isTileChecked(currentBoard, this.currentBoard.currentPlayer(), tileKing);

        boolean aPieceCanMove = false;
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                Piece piece = currentBoard.squares()[x][y].piece();
                if (piece != null && piece.color() == this.currentBoard.currentPlayer()) {
                    List<Tile> legalMoves = this.getLegalMoves(currentBoard.squares()[x][y].tile());
                    if (!legalMoves.isEmpty()) {
                        aPieceCanMove = true;
                        break;
                    }
                }
            }
        }

        if (!aPieceCanMove) {
            if(kingChecked) {
                return MoveFeedBack.CHECKMATED;
            } else {
                return MoveFeedBack.STALEMATED;
            }
        }

        return MoveFeedBack.RUNNING;
    }

    public void rollbackPreviousState() {
        if (this.historicalBoards.isEmpty()) {
            return;
        }

        // Get the last position
        HistoricalBoardFen previous = this.historicalBoards.get(this.historicalBoards.size() - 1);

        // Replace the current board with the previous board
        currentBoard = FenUtils.fenToBoard(previous.fen());

        // Replace the actual move with the previous move
        actualMove = previous.previousMove();

        // Remove the last index
        this.historicalBoards.remove(this.historicalBoards.size() - 1);
    }

    public List<Tile> getLegalMoves(Tile start) {
        // Check if the start position contains a piece
        Piece piece = currentBoard.get(start).piece();
        if (piece == null) {
            return new ArrayList<>();
        }

        // Get all the moves from the piece
        List<Tile> moves = piece.getMoves(this.currentBoard, start);

        List<Tile> movesLegal = new ArrayList<>();

        // Filter the moves to get only those who always make our king in safe mode
        String fenBoardClone = FenUtils.boardToFen(this.currentBoard);
        moves.forEach(move -> {
            Chess chessFake = new Chess(fenBoardClone, actualMove);
            chessFake.currentBoard().movePiece(start, move, null);
            boolean kingChecked = this.isKingChecked(chessFake.currentBoard(), piece.color());
            if (!kingChecked) {
                movesLegal.add(move);
            }
        });

        return movesLegal;
    }

    private MoveFeedBack isThatMoveLegal(MoveCommand move) {
        if(move == null || move.startPosition() == null || move.endPosition() == null){
            return MoveFeedBack.BAD_SELECTION;
        }

        Square startSquare = this.currentBoard.get(move.startPosition());

        // Check if the start position contains a piece
        if (startSquare.piece() == null) {
            return MoveFeedBack.NO_PIECE_SELECTED;
        }

        // Is it an ally piece ?
        if (startSquare.piece().color() != this.currentBoard.currentPlayer()) {
            return MoveFeedBack.ENNEMY_PIECE_SELECTED;
        }

        Square endSquare = this.currentBoard.get(move.endPosition());

        // Is that piece want to go on an ally piece square ?
        if (endSquare.piece() != null && endSquare.piece().color() == this.currentBoard.currentPlayer()) {
            return MoveFeedBack.PIECE_CANT_EAT_ALLY_PIECE;
        }

        // Is that piece want to go on the enemy king square ?
        if (endSquare.piece() != null && PieceType.KING == endSquare.piece().type() && endSquare.piece().color() != this.currentBoard.currentPlayer()) {
            return MoveFeedBack.CANT_EAT_ENEMY_KING;
        }

        // The possible moves doesnt contains the given move
        List<Tile> allMoves = startSquare.piece().getMoves(currentBoard, startSquare.tile());
        if (!allMoves.contains(endSquare.tile())) {
            return MoveFeedBack.PIECE_ILLEGAL_MOVE;
        }

        // Is our king is not checked when we move that piece ?
        Chess chessFake = new Chess(FenUtils.boardToFen(currentBoard), actualMove);
        chessFake.currentBoard.movePiece(move.startPosition(), move.endPosition(), move.promote());
        boolean kingChecked = this.isKingChecked(chessFake.currentBoard(), startSquare.piece().color());

        if (kingChecked) {
            return MoveFeedBack.PIECE_BLOCKED;
        }

        return MoveFeedBack.RUNNING;
    }

    private boolean isKingChecked(Board board, Color colorKing) {
        // Search our king
        return isTileChecked(board, colorKing, getTileKing(board, colorKing));
    }

    private Tile getTileKing(Board board, Color colorKing) {
        Tile kingTile = null;
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                Piece piece = board.squares()[x][y].piece();
                if (piece != null && piece.type() == PieceType.KING && piece.color() == colorKing) {
                    kingTile = board.squares()[x][y].tile();
                }
            }
        }
        return kingTile;
    }

    private boolean isTileChecked(Board board, Color allyColor, Tile tileToCheck) {
        // Get all enemy pieces, and see if one is hitting our king
        boolean isTileChecked = false;
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                Piece piece = board.squares()[x][y].piece();
                if (piece != null && piece.color() != allyColor) {
                    List<Tile> threatens = piece.getThreatens(board, board.squares()[x][y].tile());
                    if (threatens.contains(tileToCheck)) {
                        isTileChecked = true;
                        break;
                    }
                }
            }
        }

        return isTileChecked;
    }
}
