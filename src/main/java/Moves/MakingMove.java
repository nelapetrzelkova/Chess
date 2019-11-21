package Moves;

import Board.Board;

public class MakingMove {
    private final Board currentBoard;
    private final Board newBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MakingMove(Board currentBoard, Board newBoard, Move move, MoveStatus moveStatus) {
        this.currentBoard = currentBoard;
        this.newBoard = newBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    public Board getCurrentBoard() {
        return this.currentBoard;
    }

    public Board getNewBoard() {
        return this.newBoard;
    }

}
