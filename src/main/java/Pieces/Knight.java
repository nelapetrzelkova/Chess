package Pieces;

import Board.Board;
import Board.BoardUtils;
import Board.Tile;
import Moves.Move;
import Players.Team;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Knight extends Piece {

    private final static int[] POSSIBLE_MOVES = {-17, -15, -10, -6, 6, 10, 15, 17}; //all possible positions
    public Knight(Integer position, Team pieceTeam) {
        super(position, pieceTeam, PieceType.KNIGHT, true);
    }

    public Knight(Integer position, Team pieceTeam, boolean isFirstMove) {
        super(position, pieceTeam, PieceType.KNIGHT, isFirstMove);
    }


    @Override
    public List<Move> getPossibleMoves(Board board){

        int newCoords;
        final List<Move> legalMoves = new ArrayList();

        for (final int offset : POSSIBLE_MOVES) { //iterate through all possible positions
            newCoords = this.position + offset;

            if(BoardUtils.isValidCoord(newCoords)) {  //check if we are on board
                if (inFirstColumn(this.position, offset) || //handling exceptions
                        (inSecondColumn(this.position, offset)) ||
                        (inSeventhColumn(this.position, offset)) ||
                        (inEightColumn(this.position, offset))) { continue; }

                final Tile candidateTile = board.getTile(newCoords);

                if (candidateTile.empty()) { //check is tile is empty
                    legalMoves.add(new Move.MajorMove(board, this, newCoords));
                } else {  //if is occupied
                    final Piece attackedPiece = candidateTile.getPiece();

                    if (this.pieceTeam != attackedPiece.pieceTeam) { //check if we are not attacking our team
                        legalMoves.add(new Move.AttackMove(board, this, newCoords, attackedPiece));
                    }
                }
            }
        }
        return legalMoves;
    }

    @Override
    public Piece movePiece(Move move) {
        return new Knight(move.getNewCoord(), move.getPiece().pieceTeam);
    }

    //exceptions
    private static boolean inFirstColumn(int currentPosition, int offset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (offset == -17 || offset == -10 || offset == 6 || offset == 15);
    }
    private static boolean inSecondColumn(int currentPosition, int offset) {
        return BoardUtils.SECOND_COLUMN[currentPosition] && (offset == -10 || offset == 6);
    }
    private static boolean inSeventhColumn(int currentPosition, int offset) {
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (offset == -6 || offset == 10);
    }
    private static boolean inEightColumn(int currentPosition, int offset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (offset == -15 || offset == -6 || offset == 10 || offset == 17);
    }

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
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

