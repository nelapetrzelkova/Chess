package Pieces;

import Board.Board;
import Board.BoardUtils;
import Board.Tile;
import Moves.Move;
import Players.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class Bishop extends Piece {

    private final static int[] VECTOR_COORDS = {-9, -7, 7, 9}; //directions where piece can move

    public Bishop(Integer position, Team pieceTeam) {
        super(position, pieceTeam, PieceType.BISHOP, true);
    }

    public Bishop(Integer position, Team pieceTeam, boolean isFirstMove) {
        super(position, pieceTeam, PieceType.BISHOP, isFirstMove);
    }

    @Override
    public Collection<Move> getPossibleMoves(Board board) {

        final List<Move> legalMoves = new ArrayList();

        for (final int offset : VECTOR_COORDS) { //iterate through all directions
            int newCoords = this.position + offset;
            while (BoardUtils.isValidCoord(newCoords)) {
                if (inFirstColumn(this.position, offset) || inEightColumn(this.position, offset)) { break; } //exceptions
                final Tile newTile = board.getTile(newCoords);
                if (newTile.empty()) {
                    legalMoves.add(new Move.MajorMove(board, this, newCoords));
                } else {
                    final Piece attackedPiece = newTile.getPiece();
                    if(this.pieceTeam != attackedPiece.pieceTeam) { //check if we are not attacking our team
                        legalMoves.add(new Move.AttackMove(board, this, newCoords, attackedPiece));
                    }
                    break;
                }
                if (BoardUtils.FIRST_COLUMN[newCoords] || BoardUtils.EIGHTH_COLUMN[newCoords]) { break; }
                newCoords += offset; //try next tile in same direction
            }

        }

        return legalMoves;
    }

    @Override
    public Piece movePiece(Move move) {
        return new Bishop(move.getNewCoord(), move.getPiece().pieceTeam);
    }

    //exceptions
    private static boolean inFirstColumn(final int currentPosition, final int offset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (offset == -9 || offset == 7);
    }
    private static boolean inEightColumn(final int currentPosition, final int offset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (offset == -7 || offset == 9);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public int getPiecePosition() {
        return this.position;
    }

    public Team getPieceTeam() {
        return this.pieceTeam;
    }

    public boolean firstMove() { //this is for Pawn - to know if they already made a jump
        return this.isFirstMove;
    }
}
