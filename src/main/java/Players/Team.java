package Players;

import Board.BoardUtils;

public enum Team {
    WHITE {
        public int getDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean canBePromoted(int position) {
            return BoardUtils.FIRST_ROW[position];
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK {
        public int getDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean canBePromoted(int position) {
            return BoardUtils.EIGHT_ROW[position];
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    /**
     * based on piece's team we determine the direction they should move
     * @return
     */
    public abstract int getDirection();

    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract boolean canBePromoted(int position);

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}

