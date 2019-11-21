package Moves;

/**
 * to see, whether the move was done (so it didn't leave king in check etc)
 */
public enum MoveStatus {

    DONE {
        public boolean isDone() {
            return true;
        } public boolean toCheck() {
            return false;
        }},
    ILLEGAL_MOVE {
        public boolean isDone() {
            return false;
        } public boolean toCheck() {
            return false;
        }},
    TO_CHECK {
        public boolean isDone() {
            return false;
        } public boolean toCheck() {
            return true;
        }};

    public abstract boolean isDone();
    public abstract boolean toCheck();
}

