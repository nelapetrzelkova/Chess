package GUI;

import Board.Board;

import java.util.Date;

public class Clock implements Runnable {

    public long whitePlayedTime;
    public long blackPlayedTime;
    private Table table;

    Clock(Thread threadObjClock, Table table) {
        this.threadObjClock = threadObjClock;
        this.table = table;
        this.whitePlayedTime = table.whitePlayedTime;
        this.blackPlayedTime = table.blackPlayedTime;
    }

    public long getWhitePlayedTime() {
        return whitePlayedTime;
    }

    public long getBlackPlayedTime() {
        return blackPlayedTime;
    }

    private Thread threadObjClock = null;
    public void start() {
        if (threadObjClock == null) {
            threadObjClock = new Thread(this, "My Clock");
            threadObjClock.start();
        }
    }
    public void run() {
        Thread myThread = Thread.currentThread();
        while (threadObjClock == myThread) {
            Board board = table.getChessBoard();
            if (board.currentPlayer() == board.whitePlayer()) {
                whitePlayedTime = whitePlayedTime - 1;
            } else {
                blackPlayedTime = blackPlayedTime -1;
            }
            table.lowerBar.drawLowerBar(board, whitePlayedTime, blackPlayedTime);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){ }
        }
    }
    public void stop() {
        threadObjClock = null;
    }
}

