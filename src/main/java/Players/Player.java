package Players;


import Board.Board;
import Moves.MakingMove;
import Moves.Move;
import Moves.MoveStatus;
import Pieces.King;
import Pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class Player {
    protected final Board board;
    public final King king;
    public final Collection<Move> legalMoves;
    protected final boolean isInCheck;

    public Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentsMoves) {
        this.board = board;
        this.king = findKingOnBoard();
        //find out all legal moves - including castling (that's why concat is used here)
        legalMoves.addAll(calculateKingCastles(legalMoves, opponentsMoves));
        this.legalMoves = Collections.unmodifiableCollection(legalMoves);
        this.isInCheck = !Player.calculateAttacksOnTile(this.king.getPiecePosition(), opponentsMoves).isEmpty();
    }

    //check opponent's moves to see if our king is safe
    public static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> opponentsMoves) {
        final Collection<Move> attackMoves = new ArrayList();
        if (opponentsMoves.isEmpty()) { return Collections.emptyList(); }
        for (Move move : opponentsMoves) {
            if (piecePosition == move.getNewCoord()) {
                attackMoves.add(move);
            }
        }
        return attackMoves;
    }

    private King findKingOnBoard() {
        for (final Piece piece : getActivePieces()) {
            if(piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        //if king is not found that means game is over
        throw new RuntimeException("King not found");
    }


    public abstract Collection<Piece> getActivePieces();

    public abstract Team getTeam();

    public abstract Player getOpponent();

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    /**
     * when the king is in danger, check if it can escape
     * @return
     */
    protected boolean noEscape() {
        for (Move move : this.legalMoves) {
            final MakingMove moving = makeMove(move);
            if(moving.getMoveStatus().isDone()) {
                return false;
            }
        }
        return true;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && noEscape();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && noEscape();
    }

    public boolean isCastle() {
        return false;
    }


    /**
     * make move with given piece and transforms board
     * @param move
     * @return
     */
    public MakingMove makeMove(Move move) {
        if (!isMoveLegal(move)) {
            return new MakingMove(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board newBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(newBoard.currentPlayer().getOpponent().findKingOnBoard().getPiecePosition(),
                newBoard.currentPlayer().getLegalMoves());

        if (!kingAttacks.isEmpty()) {
            return new MakingMove(this.board, this.board, move, MoveStatus.TO_CHECK);
        }
        return new MakingMove(this.board, newBoard, move, MoveStatus.DONE);
    }

    public Collection<Move> getLegalMoves() {
        return this.board.currentPlayer().legalMoves;
    }


    public abstract Collection<Move> calculateKingCastles(Collection<Move> legalMoves, Collection<Move> opponentsMoves);

    public MakingMove unMakeMove(Move lastMove) {
        return new MakingMove(this.board, lastMove.undo(), lastMove, MoveStatus.DONE);
    }
}
