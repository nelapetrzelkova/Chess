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

public class Queen extends Piece {

    private final static int[] VECTOR_COORDS = {-9, -8, -7, -1, 1, 7, 8, 9}; //directions where piece can move

    public Queen(Integer position, Team pieceTeam) {
        super(position, pieceTeam, PieceType.QUEEN, true);
    }

    public Queen(Integer position, Team pieceTeam, boolean isFirstMove) {
        super(position, pieceTeam, PieceType.QUEEN, isFirstMove);
    }

    @Override
    public Collection<Move> getPossibleMoves(Board board) {

        final List<Move> legalMoves = new ArrayList();

        for (final int offset : VECTOR_COORDS) { //iterate through all directions
            int newCoords = this.position + offset;
            while (BoardUtils.isValidCoord(newCoords)) {
                if (inFirstColumn(this.position, offset) || inEightColumn(this.position, offset)) { break; }
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
                if ((BoardUtils.FIRST_COLUMN[newCoords] && ( offset == -9 ||  offset == -1 || offset == 7)) ||
                        ((BoardUtils.EIGHTH_COLUMN[newCoords]) && ( offset == 9 ||  offset == 1 || offset == -7))) { break; }
                newCoords += offset; //try next tile in same direction
            }

        }

        return Collections.unmodifiableCollection(legalMoves);
    }

    private static boolean inFirstColumn(int currentPosition, int offset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (offset == -9 || offset == -1 || offset == 7);
    }
    private static boolean inEightColumn(int currentPosition, int offset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (offset == -7 || offset == 1 || offset == 9);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Queen(move.getNewCoord(), move.getPiece().pieceTeam);
    }

    @Override
    public String toString() {
        return PieceType.QUEEN.toString();
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

