import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Knight extends Piece{
    public Knight(ImageIcon icon, String name, int xPos, int yPos) {
        super(icon, name, xPos, yPos);
        pieceType = "Knight";
        material = 3; 
    }

    @Override
    public boolean canMove(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        if (outOfBoard(x, y)) {
            return false;
        }
        Piece potTarget = board.getPieces()[x][y];
        if (potTarget != null && potTarget.isWhite() == isWhite) {
            return false;
        }
        int maxDif = Math.max(Math.abs(xPos - x), Math.abs(yPos - y));
        if (!(Math.pow(Math.abs(xPos - x), 2) + Math.pow(Math.abs(yPos - y), 2) == 5 && maxDif == 2)) {
            return false;
        }
        if (!canMoveThroughCheck(x, y)) {
            return false;
        }

        
        return true;
    }
    @Override
    public boolean canAttack(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        Piece target = board.getPieces()[x][y];
        if (target != null && target.isWhite() == isWhite) {
            return false;
        }
        
        int maxDif = Math.max(Math.abs(xPos - x), Math.abs(yPos - y));
        if (Math.pow(Math.abs(xPos - x), 2) + Math.pow(Math.abs(yPos - y), 2) == 5 && maxDif == 2) {
            return true;
        }
        
        return false;
    }

    public boolean outOfBoard(int x, int y) {
        return x > Globals.COLS - 1 || y > Globals.ROWS - 1 || x < 0 || y < 0;
    }

    @Override
    public int[][] getValidMoves() {
        ArrayList<int[]> validMoves = new ArrayList<>();
        if (canMove(xPos + 1, yPos + 2)) {
            validMoves.add(new int[]{xPos + 1, yPos + 2});
        }
        if (canMove(xPos + 1, yPos - 2)) {
            validMoves.add(new int[]{xPos + 1, yPos - 2});
        }
        if (canMove(xPos - 1, yPos + 2)) {
            validMoves.add(new int[]{xPos - 1, yPos + 2});
        }
        if (canMove(xPos - 1, yPos - 2)) {
            validMoves.add(new int[]{xPos - 1, yPos - 2});
        }
        if (canMove(xPos + 2, yPos + 1)) {
            validMoves.add(new int[]{xPos + 2, yPos + 1});
        }
        if (canMove(xPos + 2, yPos - 1)) {
            validMoves.add(new int[]{xPos + 2, yPos - 1});
        }
        if (canMove(xPos - 2, yPos + 1)) {
            validMoves.add(new int[]{xPos - 2, yPos + 1});
        }
        if (canMove(xPos - 2, yPos - 1)) {
            validMoves.add(new int[]{xPos - 2, yPos - 1});
        }
        return validMoves.toArray(new int[validMoves.size()][]);
    }


}
