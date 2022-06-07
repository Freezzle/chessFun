package ch.claudedy.chess.basis;

import ch.claudedy.chess.systems.ConsolePrint;
import ch.claudedy.chess.systems.SystemConfig;
import ch.claudedy.chess.utils.FenUtils;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@Getter
public class Chess {

    private final List<HistoricalBoardFen> historicalBoards;
    private MoveCommand actualMove;
    private final Board currentBoard;
    private GameStatus gameStatus;

    public Chess(String fen, MoveCommand previousMove) {
        this.currentBoard = FenUtils.fenToBoard(fen);
        this.historicalBoards = new ArrayList<>();
        this.actualMove = previousMove;
        this.gameStatus = GameStatus.WAITING_MOVE;
    }

    public MoveStatus makeMove(MoveCommand move) {
        if (!this.gameStatus.isGameWaitingMove()) {
            return MoveStatus.CANT_MOVE_DURING_ANOTHER_MOVE;
        }

        if (this.gameStatus.isGameOver()) {
            return MoveStatus.GAME_ALREADY_OVER;
        }

        this.gameStatus = GameStatus.EXECUTING;

        // Check if the move is authorized
        MoveStatus statusMoveDone = this.isThatMoveLegal(move);

        if (statusMoveDone == MoveStatus.OK) {
            // Create a copy of the board before to make action
            this.historicalBoards.add(new HistoricalBoardFen().fen(FenUtils.boardToFen(this.currentBoard)).previousMove(this.actualMove));

            // The move action become the actual move
            this.actualMove = move;

            // Move the piece
            this.currentBoard.movePiece(move.startPosition(), move.endPosition(), move.promote());

            // Switch the player
            this.currentBoard.switchPlayer();

            // Check if the enemy king is checkmated, etc..
            this.synchroGameStatus();

            if (!this.gameStatus.isGameOver()) {
                this.gameStatus = GameStatus.WAITING_MOVE;
            }

            if (SystemConfig.PRINT_CONSOLE) {
                ConsolePrint.execute(this);
            }
        } else {
            this.gameStatus = GameStatus.WAITING_MOVE;
        }

        return statusMoveDone;
    }

    private void synchroGameStatus() {

        if (this.currentBoard.fiftyRules() == 50) {
            gameStatus = GameStatus.RULES_50;
            return;
        }

        // Threefold repetition rule
        HistoricalBoardFen fenCurrentBoard = new HistoricalBoardFen().fen(FenUtils.boardToFen(this.currentBoard)).previousMove(this.actualMove);
        int nbTimes = Collections.frequency(this.historicalBoards.stream().map(HistoricalBoardFen::onlyBoardFen).collect(Collectors.toList()), fenCurrentBoard.onlyBoardFen());
        if (nbTimes >= 3) {
            gameStatus = GameStatus.REPETITION_RULE;
            return;
        }

        // King is checked
        Tile tileKing = currentBoard.getTileKing(this.currentBoard.currentPlayer());

        boolean cannotMove = getLegalMoves(tileKing).isEmpty();

        if (cannotMove) {
            // King can't move !
            boolean kingChecked = currentBoard.isTileChecked(currentBoard.currentPlayer(), tileKing);

            boolean aPieceCanMove = false;
            for (int x = 0; x <= 7; x++) {
                for (int y = 0; y <= 7; y++) {
                    Piece piece = currentBoard.squares()[x][y].piece();
                    if (piece != null && piece.color() == this.currentBoard.currentPlayer()) {
                        List<PossibleMove> legalMoves = this.getLegalMoves(currentBoard.squares()[x][y].tile());
                        if (!legalMoves.isEmpty()) {
                            aPieceCanMove = true;
                            break;
                        }
                    }
                }
            }

            if (!aPieceCanMove && kingChecked) {
                gameStatus = GameStatus.CHECKMATED;
            } else if (!aPieceCanMove && !kingChecked) {
                gameStatus = GameStatus.STALEMATED;
            }
        } else {
            // King can move !

            // IMPOSSIBLE CHECKMATE
            List<Piece> allPieces = new ArrayList<>();
            allPieces.addAll(currentBoard.getAlivePieces(Color.WHITE));
            allPieces.addAll(currentBoard.getAlivePieces(Color.BLACK));

            if (allPieces.size() == 2) {
                // king vs king
                gameStatus = GameStatus.IMPOSSIBILITY_CHECKMATE;
            } else if (allPieces.size() == 3) {
                if (allPieces.stream().anyMatch(p -> p.type() == PieceType.BISHOP) || allPieces.stream().anyMatch(p -> p.type() == PieceType.KNIGHT)) {
                    // king + bishop vs king || king + knight vs king
                    gameStatus = GameStatus.IMPOSSIBILITY_CHECKMATE;
                }
            } else if (allPieces.size() == 4) {
                List<Square> bishopsSquares = currentBoard.getAllPiecesSquares().stream().filter(s -> s.piece().type() == PieceType.BISHOP).collect(Collectors.toList());

                if (bishopsSquares.size() == 2 && bishopsSquares.get(0).tile().color().isSameColor(bishopsSquares.get(1).tile().color())) {
                    // king + bishop vs king + bishop (bishop same color)
                    gameStatus = GameStatus.IMPOSSIBILITY_CHECKMATE;
                }
            }
        }
    }

    public List<PossibleMove> getLegalMoves(Tile start) {
        // Check if the start position contains a piece
        Piece piece = currentBoard.getSquare(start).piece();
        if (piece == null) {
            return new ArrayList<>();
        }

        // Get all the moves from the piece
        List<PossibleMove> moves = piece.getMoves(this.currentBoard, start);

        // Remove all move to go on the enemy king (can't eat him)
        moves = moves.stream().filter(move -> move.type() != MoveType.THREAT_ENEMY_KING).collect(Collectors.toList());

        // Remove all possibilites to castle when the piece is the king and is checked
        if (piece.type() == PieceType.KING && currentBoard.isKingChecked(piece.color())) {
            moves = moves.stream().filter(move -> move.type() != MoveType.CASTLE).collect(Collectors.toList());
        }

        List<PossibleMove> legals = new ArrayList<>();
        String fenBoardClone = FenUtils.boardToFen(this.currentBoard);
        for (PossibleMove currentMove : moves) {
            // Filter the moves to get only those who always make our king in safe mode
            Chess chessFake = new Chess(fenBoardClone, actualMove);
            chessFake.currentBoard().movePiece(start, currentMove.destination(), null);
            if (!chessFake.currentBoard().isKingChecked(piece.color())) {
                legals.add(currentMove);
            }
        }

        return legals;
    }

    private MoveStatus isThatMoveLegal(MoveCommand move) {
        if (move == null || move.startPosition() == null || move.endPosition() == null) {
            return MoveStatus.BAD_MOVE_COMMAND;
        }

        Square startSquare = this.currentBoard.getSquare(move.startPosition());

        // Check if the start position contains a piece
        if (startSquare.piece() == null) {
            return MoveStatus.NO_PIECE_SELECTED;
        }

        // Is it an ally piece ?
        if (startSquare.piece().color() != this.currentBoard.currentPlayer()) {
            return MoveStatus.ENNEMY_PIECE_SELECTED;
        }

        Square endSquare = this.currentBoard.getSquare(move.endPosition());

        // Is that piece want to go on an ally piece square ?
        if (endSquare.piece() != null && endSquare.piece().color() == this.currentBoard.currentPlayer()) {
            return MoveStatus.PIECE_CANT_EAT_ALLY_PIECE;
        }

        // Is that piece want to go on the enemy king square ?
        if (endSquare.piece() != null && PieceType.KING == endSquare.piece().type() && endSquare.piece().color() != this.currentBoard.currentPlayer()) {
            return MoveStatus.CANT_EAT_ENEMY_KING;
        }

        // The possible moves doesnt contains the given move
        List<PossibleMove> allMoves = getLegalMoves(startSquare.tile());
        if (allMoves.stream().noneMatch(m -> m.destination() == endSquare.tile())) {
            return MoveStatus.PIECE_ILLEGAL_MOVE;
        }

        // Is our king is not checked when we move that piece ?
        Chess chessFake = new Chess(FenUtils.boardToFen(currentBoard), actualMove);
        chessFake.currentBoard.movePiece(move.startPosition(), move.endPosition(), move.promote());
        boolean kingChecked = chessFake.currentBoard().isKingChecked(startSquare.piece().color());

        if (kingChecked) {
            return MoveStatus.PIECE_BLOCKED;
        }

        return MoveStatus.OK;
    }
}
