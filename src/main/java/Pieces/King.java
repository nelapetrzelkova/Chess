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

public class King extends Piece {

    private final static int[] POSSIBLE_MOVES = {-9, -8, -7, -1, 1, 7, 8, 9}; //all possible positions

    public King(Integer position, Team pieceTeam) {
        super(position, pieceTeam, PieceType.KING, true);
    }

    public King(Integer position, Team pieceTeam, boolean isFirstMove) {
        super(position, pieceTeam, PieceType.KING, isFirstMove);
    }

    @Override
    public Collection<Move> getPossibleMoves(Board board) {

        final List<Move> legalMoves = new ArrayList();

        for (final int offset : POSSIBLE_MOVES) { //iterate through all possible moves
            int newCoords = this.position + offset;
            if (BoardUtils.isValidCoord(newCoords)) { //check if we are on board
                if (inFirstColumn(this.position, offset) || inEightColumn(this.position, offset)) { continue; } //exceptions
                final Tile newTile = board.getTile(newCoords);
                if (newTile.empty()) { //check is tile is empty
                    legalMoves.add(new Move.MajorMove(board, this, newCoords));
                } else { //if is occupied
                    final Piece attackedPiece = newTile.getPiece();
                    if(this.pieceTeam != attackedPiece.pieceTeam) { //check if we are not attacking our team
                        legalMoves.add(new Move.AttackMove(board, this, newCoords, attackedPiece));
                    }
                }
            }
        }

        return Collections.unmodifiableCollection(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        return new King(move.getNewCoord(), move.getPiece().pieceTeam);
    }

    //exceptions
    private static boolean inFirstColumn(int currentPosition, int offset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (offset == -9 || offset == -1 || offset == 7);
    }

    private static boolean inEightColumn(int currentPosition, int offset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (offset == -7 || offset == 1 || offset == 9);
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
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

