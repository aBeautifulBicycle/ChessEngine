import java.util.Arrays;

public class Move {
    private int[] moveLocation;
    private Piece movePiece;
    private double moveEvaluation;
    private double movePriority = 0;
    public Move(int[] moveLocation, Piece movePiece) {
        this.moveLocation = moveLocation;
        this.movePiece = movePiece;
        if (movePiece.isWhite()) {
            moveEvaluation = -100000;
        } else {
            moveEvaluation = 100000;
        }
    }
    public int[] getMoveLocation() {
        return moveLocation;
    }
    public void setMoveLocation(int[] moveLocation) {
        this.moveLocation = moveLocation;
    }
    public Piece getMovePiece() {
        return movePiece;
    }
    public void setMovePiece(Piece movePiece) {
        this.movePiece = movePiece;
    }
    @Override
    public String toString() {
        return movePiece + " " + Arrays.toString(moveLocation) + " " + moveEvaluation + " " + movePriority;
    }
    public double getMoveEvaluation() {
        return moveEvaluation;
    }
    public void setMoveEvaluation(double moveEvaluation) {
        this.moveEvaluation = moveEvaluation;
    }
    public double getMovePriority() {
        return movePriority;
    }
    public void setMovePriority(double movePriority) {
        this.movePriority = movePriority;
    }
}
