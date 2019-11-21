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


public class Rook extends Piece { //vez

    private final static int[] VECTOR_COORDS = {-8, -1, 1, 8}; //directions where piece can move

    public Rook(Integer position, Team pieceTeam) {
        super(position, pieceTeam, PieceType.ROOK, true);
    }

    public Rook(Integer position, Team pieceTeam, boolean isFirstMove) {
        super(position, pieceTeam, PieceType.ROOK, isFirstMove);
    }
    @Override
    public Collection<Move> getPossibleMoves(Board board) {

        final List<Move> legalMoves;
        legalMoves = new ArrayList();

        for (final int offset : VECTOR_COORDS) { //iterate through all directions
            int newCoords = this.position + offset;
            while (BoardUtils.isValidCoord(newCoords)) { //check if we are still on board
                if (inFirstColumn(this.position, offset) || inEightColumn(this.position, offset)) { break; } //exceptions when we are in the last column
                final Tile newTile = board.getTile(newCoords);
                if (newTile.empty()) { //if new tile is empty
                    legalMoves.add(new Move.MajorMove(board, this, newCoords));
                } else { //if new tile is occupied
                    final Piece attackedPiece = newTile.getPiece();
                    if (this.pieceTeam != attackedPiece.pieceTeam) { //check if we are not attacking our team
                        legalMoves.add(new Move.AttackMove(board, this, newCoords, attackedPiece));
                    }
                    break;
                }
                if ((BoardUtils.FIRST_COLUMN[newCoords] && offset == -1) || (BoardUtils.EIGHTH_COLUMN[newCoords] && offset == 1)) { break; }
                newCoords += offset; //try next tile in same direction
            }

        }

        return legalMoves;
    }

    //exceptions
    private static boolean inFirstColumn(int currentPosition, int offset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (offset == -1);
    }
    private static boolean inEightColumn(int currentPosition, int offset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (offset == 1);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Rook(move.getNewCoord(), move.getPiece().pieceTeam);
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
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

